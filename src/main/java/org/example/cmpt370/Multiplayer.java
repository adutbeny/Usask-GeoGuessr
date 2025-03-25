package org.example.cmpt370;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class Multiplayer {
    private FirebaseHelper fbHelper;
    private String playerUid;

    public Multiplayer(String playerUid) {
        this.playerUid = playerUid; // this will have to be the username of the player, or maybe the hash??
        fbHelper = new FirebaseHelper();
    }
    /** joins the wating cue for multiplayer **/
    public void joinWaitQueue() {
        String waitingJson = "{\"timestamp\": " + System.currentTimeMillis() + "}";
        fbHelper.writeData("waiting/" + playerUid, waitingJson);
    }


    /* trys to match two players by checking waiting node queue, if two players,
    creates a match with all necessary nodes and writes to server
    returns true if match created and false otherwise */
    public boolean MatchPlayers() {
        // reads the waiting node from firebase
        String waitingData = fbHelper.readData("waiting");
        if (waitingData == null || waitingData.equals("null")) {
            System.out.println("no players found gl");
            return false;
        }
        // parse the json into a map we can look at
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> waitingMap = gson.fromJson(waitingData, type);

        // check if there are two players in the wait queue
        if (waitingMap.size() >= 2) {
            Iterator<String> it = waitingMap.keySet().iterator();
            String player1 = it.next();
            String player2 = it.next();
            // make a match id here by using current time, not sure what else we could use
            String matchId = "match-" + System.currentTimeMillis();

            // build a match json string to write to firebase
            String matchJson = "{"
                    + "\"players\": {"
                    + "\"playerA\": \"" + player1 + "\","
                    + "\"playerB\": \"" + player2 + "\""
                    + "},"
                    + "\"guesses\": {},"
                    + "\"chat\": {}"
                    + "}";
            // write to new matches node in firebase
            fbHelper.writeData("matches/" + matchId, matchJson);

            // removes the players from wait queue
            fbHelper.writeData("waiting/" + player1, "null");
            fbHelper.writeData("waiting/" + player2, "null");

            System.out.println("match created: " + matchId + " for players " + player1 + " and " + player2); // debugging
            return true;
        } else {
            System.out.println("no playes found"); // debugging delete later
            return false;
        }
    }

    /** records the guess for a player and writes data to server */
    public void recordGuess(String matchId, double lat, double lng) {
        String guessJson = String.format("{ \"lat\": %f, \"lng\": %f }", lat, lng);
        fbHelper.writeData("matches/" + matchId + "/guesses/" + playerUid, guessJson);
    }

    public void clearGuesses(String matchId, int roundNumber) {
        // Write "null" to the guesses node to remove it.
        fbHelper.writeData("matches/" + matchId + "/guesses", "null");
    }

    public double[] readOpponentGuess(String matchId) {
        // Rread guess node from firebase
        String jsonData = fbHelper.readData("matches/" + matchId + "/guesses");
        if (jsonData == null || jsonData.equals("null")) {
            System.out.println("no guess data found"); // debugging deelte later
            return null;
        }

        // parse json into map we can read
        Gson gson = new Gson();
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

    public void sendChatMessage(String matchId, String messageText) {
        String messageKey = "msg" + System.currentTimeMillis();
        String chatJson = String.format("{ \"sender\": \"%s\", \"text\": \"%s\", \"timestamp\": %d }",
                playerUid, messageText, System.currentTimeMillis());
        fbHelper.writeData("matches/" + matchId + "/chat/" + messageKey, chatJson);
    }
    // TODO: something to receive chats
}