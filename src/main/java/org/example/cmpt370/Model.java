package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import java.util.*;

enum DISPLAY {
    STARTUP,
    DIFF,
    GAMEPLAY,
    LEADERBOARD
    //etc.
}

/** Class that manages all the data
 * Controller calls Model methods to act on things like pictures
 * This is where we pass in the coordinates of mouse locations */
public class Model {

    // Model attributes
    private ArrayList<Subscriber> subscribers;
    private DISPLAY currentWindow;
    // user attributes
    private String username;
    private int score;
    private int highscore;
    private int round;
    // etc...

    /** Constructor */
    public Model() {
        this.subscribers = new ArrayList<Subscriber>();
        this.round = 1;
        this.currentWindow = DISPLAY.STARTUP;
    }

    /** Add any displays to the list of objects updated on
     * every change to Model
     * @param sub object to be added */
    public void addSubscriber(Subscriber sub) {
        this.subscribers.add(sub);
    }
    public void notifySubscribers() {
        for (Subscriber sub : this.subscribers) {
            sub.modelUpdated();
        }
    }

    /* TODO:
     * things we will probably need
     * calculateDistance()
     * nextImage()
     * showMap()
     * selectImageSet()
     * etc
     */

    /** GETTERS */
    public DISPLAY getCurrentWindow() {
        return currentWindow;
    }
    public String getUsername() {
        return username;
    }
    public int getScore() {
        return score;
    }
    public int getHighscore() {
        return highscore;
    }
    public int getRound() {
        return round;
    }

    /** Prompts View to show select difficulty display */
    public void showDifficultyWindow() {
        this.currentWindow = DISPLAY.DIFF;
        notifySubscribers();
    }

    /** Prompts View to show main gameplay window */
    public void showGameplayWindow() {
        this.currentWindow = DISPLAY.GAMEPLAY;
        notifySubscribers();
    }

}
