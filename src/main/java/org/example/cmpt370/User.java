package org.example.cmpt370;

/** Class to store user information on a successful login */
public class User {

    private String username;
    private int score;
    private int highscore;

    /** Constructor */
    public User() {
        // set up user stuff here, pulled from DB
    }

    /** Getters */
    public String getUsername() {
        return username;
    }
    public int getScore() {
        return score;
    }
    public int getHighscore() {
        return highscore;
    }
}
