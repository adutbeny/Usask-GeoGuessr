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
        this.playerUid = playerUid;
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
    public void MatchPlayers() {
        // reads the waiting node from firebase
        String waitingData = fbHelper.readData("waiting");
        if (waitingData == null || waitingData.equals("null")) {
            System.out.println("No waiting players found.");
            return;
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

            System.out.println("Match created: " + matchId + " for players " + player1 + " and " + player2);
        } else {
            System.out.println("Not enough waiting players for a match.");
        }
    }

    /** records the guess for a player and writes data to server */
    public void recordGuess(String matchId, double lat, double lng) {
        String guessJson = String.format("{ \"lat\": %f, \"lng\": %f }", lat, lng);
        fbHelper.writeData("matches/" + matchId + "/guesses/" + playerUid, guessJson);
    }

}