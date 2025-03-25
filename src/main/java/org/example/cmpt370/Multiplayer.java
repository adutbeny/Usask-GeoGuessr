package org.example.cmpt370;

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
    /* creates a match in with all necessary nodes and writes to server */
    public void createMatch(String opponentUid, String matchId) {
        String matchJson = "{" + "\"players\": {" + "\"playerA\": \"" + playerUid + "\","
                + "\"playerB\": \"" + opponentUid + "\"" + "}," + "\"guesses\": {}," + "\"chat\": {}" + "}";
        fbHelper.writeData("matches/" + matchId, matchJson);
    }

    /** records the guess for a player and writes data to server */
    public void recordGuess(String matchId, double lat, double lng) {
        String guessJson = String.format("{ \"lat\": %f, \"lng\": %f }", lat, lng);
        fbHelper.writeData("matches/" + matchId + "/guesses/" + playerUid, guessJson);
    }

}