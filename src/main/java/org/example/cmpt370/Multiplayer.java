package org.example.cmpt370;

public class Multiplayer {
    private FirebaseHelper fbHelper;
    private String playerUid;

    public Multiplayer(String playerUid) {
        this.playerUid = playerUid;
        fbHelper = new FirebaseHelper();
    }
}
