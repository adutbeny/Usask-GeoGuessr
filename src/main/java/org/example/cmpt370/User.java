package org.example.cmpt370;

/** Class to store user information on a successful login */
public class User {

    private String username;
    private int score;
    private int N_highscore;
    private int S_highscore;
    private int E_highscore;

    /** Constructor */
    public User(String user, int novice_highscore, int seasonal_highscore, int expert_highscore, int currentscore) {
        // set up user stuff here, pulled from DB
        this.username = user;
        this.N_highscore = novice_highscore;
        this.S_highscore = seasonal_highscore;
        this.E_highscore = expert_highscore;
        this.score = currentscore;
    }

    /** Getters */
    public String getUsername() {
        return username;
    }
    public int getScore() {
        return score;
    }
    public int getNoviceHighscore() {
        return N_highscore;
    }
    public int getSeasonalHighscore() {
        return S_highscore;
    }
    public int getExpertHighscore() {
        return E_highscore;
    }
    public void setN_highscore(int score){
        this.N_highscore = score;
    }
    public void setS_highscore(int score){
        this.S_highscore = score;
    }
    public void setE_highscore(int score){
        this.E_highscore = score;
    }
}
