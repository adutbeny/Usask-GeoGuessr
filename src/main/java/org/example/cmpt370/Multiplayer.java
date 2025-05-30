package org.example.cmpt370;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class Multiplayer {
    private FirebaseHelper fbHelper;
    private String playerUid;
    private String opponentUid;
    private long lastTimestamp = 0;
    private String currentMatchId;
    private Gson gson = new Gson();

    public Multiplayer(String playerUid) {
        this.playerUid = playerUid; // this will have to be the username of the player, or maybe the hash??
        this.opponentUid = null;
        fbHelper = new FirebaseHelper();
    }
    /** joins the wating cue for multiplayer **/
    public void joinWaitQueue() {
        this.opponentUid = null;
        String waitingJson = "{\"timestamp\": " + System.currentTimeMillis() + "}";
        fbHelper.writeData("waiting/" + playerUid, waitingJson);
    }


    /* trys to match two players by checking waiting node queue, if two players,
    creates a match with all necessary nodes and writes to server
    returns true if match created and false otherwise */
    public String matchPlayers() {
        // reads the waiting node from firebase
        String waitingData = fbHelper.readData("waiting");
        if (waitingData == null || waitingData.equals("null")) {
            System.out.println("No waiting players found.");
            return null;
        }
        // parse waiting data into a map
        Type type = new TypeToken<Map<String, Map<String, Long>>>() {}.getType();
        Map<String, Map<String, Long>> waitingMap = gson.fromJson(waitingData, type);
        if (waitingMap == null || waitingMap.size() < 2) {
            System.out.println("Not enough players waiting for a match.");
            return null;
        }

        // cleans wait queue of entries that are 60 seconds or longer
        long currentTime = System.currentTimeMillis();
        long threshold = 60000; // 60 seconds limit
        Iterator<Map.Entry<String, Map<String, Long>>> it = waitingMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<String, Long>> entry = it.next();
            long ts = entry.getValue().get("timestamp");
            if (currentTime - ts > threshold) {
                System.out.println("Removing stale entry for player: " + entry.getKey());
                fbHelper.writeData("waiting/" + entry.getKey(), "null");
                it.remove();
            }
        }

        // After cleanup, check if enough players remain.
        if (waitingMap.size() < 2) {
            System.out.println("Not enough players waiting after cleanup.");
            return null;
        }

        // find player with earliest timestamp
        // this is how we try to force one central match node between the two players
        String earliestPlayer = null;
        long earliestTimestamp = Long.MAX_VALUE;
        for (Map.Entry<String, Map<String, Long>> entry : waitingMap.entrySet()) {
            long ts = entry.getValue().get("timestamp");
            if (ts < earliestTimestamp) {
                earliestTimestamp = ts;
                earliestPlayer = entry.getKey();
            }
        }
        // if not host wait for host to create match
        if (!playerUid.equals(earliestPlayer)) {
            System.out.println("waiting for host to create match");
            return null;
        }
        // remove the host from waiting map so we can choose an opponent.
        waitingMap.remove(playerUid);
        if (waitingMap.isEmpty()) {
            System.out.println("no opponent found");
            return null;
        }
        // pick an opponent here pick first remaining player.
        String opponent = waitingMap.keySet().iterator().next();
        // create a matchID using the current timestamp
        String matchId = "match-" + System.currentTimeMillis();
        this.currentMatchId = matchId;
        // build match json data.
        String matchJson = "{"
                + "\"players\": {"
                + "\"playerA\": \"" + playerUid + "\","
                + "\"playerB\": \"" + opponent + "\""
                + "},"
                + "\"guesses\": {},"
                + "\"chat\": {}"
                + "}";
        // write the match node.
        fbHelper.writeData("matches/" + matchId, matchJson);
        // removes both players from the waiting queue.
        fbHelper.writeData("waiting/" + playerUid, "null");
        fbHelper.writeData("waiting/" + opponent, "null");

        this.opponentUid = opponent;
        System.out.println("Match created: " + matchId + " for players " + playerUid + " and " + opponent);
        return matchId;
    }

    public String findMatchForOpponent() {
        String waitingData = fbHelper.readData("waiting");
        if (waitingData == null || waitingData.equals("null")) {
            System.out.println("No waiting players found.");
            return null;
        }
        // parse data from wait queue to clean old entires
        Type type = new TypeToken<Map<String, Map<String, Long>>>() {}.getType();
        Map<String, Map<String, Long>> waitingMap = gson.fromJson(waitingData, type);

        // clean entries older than 60 seconds
        long currentTime = System.currentTimeMillis();
        long threshold = 60000; // 60 seconds limit
        Iterator<Map.Entry<String, Map<String, Long>>> it = waitingMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<String, Long>> entry = it.next();
            long ts = entry.getValue().get("timestamp");
            if (currentTime - ts > threshold) {
                System.out.println("Removing stale waiting entry for player: " + entry.getKey());
                fbHelper.writeData("waiting/" + entry.getKey(), "null");
                it.remove();
            }
        }

        String matchesData = fbHelper.readData("matches");
        if (matchesData == null || matchesData.equals("null")) {
            System.out.println("no matches found");
            return null;
        }
        Type matchType = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
        Map<String, Map<String, Object>> matchesMap = gson.fromJson(matchesData, matchType);
        if (matchesMap == null || matchesMap.isEmpty()) {
            System.out.println("matches map empty");
            return null;
        }
        // look through all matches to look for if theres one where current playersid is in it
        // checks if a match has been made for player and we store the match id so that we can use
        // it later for other functionality, this forces one central node in the db to be made for
        // each round
        for (Map.Entry<String, Map<String, Object>> entry : matchesMap.entrySet()) {
            String matchId = entry.getKey();
            Map<String, Object> matchData = entry.getValue();
            if (matchData.containsKey("players")) {
                Map<String, String> players = (Map<String, String>) matchData.get("players");
                if (players.containsValue(playerUid)) {
                    currentMatchId = matchId; // store it for later use
                    System.out.println("found match for opponent " + matchId);
                    if (players.get("playerA").equals(playerUid)) {
                        this.opponentUid = players.get("playerB");
                    } else {
                        this.opponentUid = players.get("playerA");
                    }
                    System.out.println("Found match for opponent: " + matchId);
                    return matchId;
                }
            }
        }
        System.out.println("no match found for opponont");
        return null;
    }

    /** records the guess for a player and writes data to server */
    public void recordGuess(String matchId, double lat, double lng) {
        String guessJson = String.format("{ \"lat\": %f, \"lng\": %f }", lat, lng);
        fbHelper.writeData("matches/" + matchId + "/guesses/" + playerUid, guessJson);
    }

    public void clearGuesses(String matchId) {
        // Write "null" to the guesses node to remove it.
        fbHelper.writeData("matches/" + matchId + "/guesses", "null");
    }

    /** Returns the lat and long coordinates of the opponents guess */
    public double[] readOpponentGuess(String matchId) {
        // read guess node from firebase
        String jsonData = fbHelper.readData("matches/" + matchId + "/guesses");
        if (jsonData == null || jsonData.equals("null")) {
            System.out.println("no guess data found"); // debugging deelte later
            return null;
        }

        // parse json into map we can read
        Type type = new TypeToken<Map<String, Map<String, Double>>>(){}.getType();
        Map<String, Map<String, Double>> guesses = gson.fromJson(jsonData, type);

        if (guesses == null || guesses.isEmpty()) {
            System.out.println("no guesses in match"); // debugging can delete later
            return null;
        }

        // get guess that does not belong to the player
        for (Map.Entry<String, Map<String, Double>> entry : guesses.entrySet()) {
            if (!entry.getKey().equals(playerUid)) {
                Map<String, Double> opponentGuess = entry.getValue();
                double lat = opponentGuess.get("lat");
                double lng = opponentGuess.get("lng");
                System.out.println("Opponent guess: " + lat + ", " + lng); //for debugging delete later
                return new double[] { lat, lng };
            }
        }
        System.out.println("guess not found");
        return null;
    }

    public static class ChatMessage {
        public String sender;
        public String text;
        public long timestamp;
    }
    // send a chat message to the other player
    public void sendChatMessage(String matchId, String messageText) {
        String messageKey = "msg" + System.currentTimeMillis();
        String chatJson = String.format("{ \"sender\": \"%s\", \"text\": \"%s\", \"timestamp\": %d }",
                this.playerUid, messageText, System.currentTimeMillis());
        this.fbHelper.writeData("matches/" + matchId + "/chat/" + messageKey, chatJson);
    }

    /** receives messages that are not your own and are only the latest used message */
    public String receiveChatMessage() {
        // reads all chat nodes from firebase
        String chatData = this.fbHelper.readData("matches/" + this.currentMatchId + "/chat");
        if (chatData == null || chatData.equals("null")) {
            return null;
        }

        // parse the json into Map where each key is a message key and each value is a ChatMessage.
        Type type = new TypeToken<Map<String, ChatMessage>>(){}.getType();
        Map<String, ChatMessage> messages = gson.fromJson(chatData, type);
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        ChatMessage latestNewMessage = null;
        // iterate over all messages
        for (ChatMessage msg : messages.values()) {
            // ignore players own messages and all messages that have already been read
            // this is a bad solution because the time to read gets worse every single time
            if (!msg.sender.equals(this.playerUid) && msg.timestamp > lastTimestamp) {
                // find most recent message
                if (latestNewMessage == null || msg.timestamp > latestNewMessage.timestamp) {
                    latestNewMessage = msg;
                }
            }
        }

        if (latestNewMessage != null) { // if we find message than update recent timestamp and return
            lastTimestamp = latestNewMessage.timestamp;
            return latestNewMessage.text;
        }

        return null;
    }
    public void clearMatches() {
        fbHelper.writeData("matches", "null");
        System.out.println("All matches cleared from Firebase.");
    }
    public String getOpponentUid() {
        return this.opponentUid;
    }
}