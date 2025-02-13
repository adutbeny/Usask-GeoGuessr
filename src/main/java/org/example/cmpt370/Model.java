package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import java.util.*;

enum DISPLAY {
    DEFAULT,
    DIFF,
    MAIN,
    LEADERBOARD
    //etc.
}

/** Class that manages all the data
 * Controller calls Model methods to act on things like pictures
 * This is where we pass in the coordinates of mouse locations */
public class Model {

    private ArrayList<Subscriber> subscribers;
    private DISPLAY currentWindow = DISPLAY.DEFAULT;

    /** Constructor */
    public Model() {
        this.subscribers = new ArrayList<Subscriber>();
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

    public DISPLAY getCurrentWindow() {
        return currentWindow;
    }

    public void showDifficultyWindow() {
        this.currentWindow = DISPLAY.DIFF;
        notifySubscribers();
    }
}
