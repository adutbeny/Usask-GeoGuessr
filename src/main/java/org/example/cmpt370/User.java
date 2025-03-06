package org.example.cmpt370;

/** Class to store user information on a successful login */
public class User {

    private String username;
    private int score;
    private int highscore;

    /** Constructor */
    public User(String user, int highscore, int currentscore) {
        // set up user stuff here, pulled from DB
        this.username = user;
        this.highscore = highscore;
        this.score = currentscore;
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
