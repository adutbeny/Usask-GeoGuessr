package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import java.util.ArrayList;
import java.util.List;

/** Class that manages all the data
 * Controller calls Model methods to act on things like pictures
 * This is where we pass in the coordinates of mouse locations */
public class Model {

    private List<Subscriber> subscribers;

    /** Constructor */
    public Model() {
        this.subscribers = new ArrayList<>();
    }

    /** Add any displays to the list of objects updated on
     * every change to Model
     * @param sub object to be added */
    public void addSubscriber(Subscriber sub) {
        this.subscribers.add(sub);
    }


    /* TODO:
     * things we will probably need
     * calculateDistance()
     * nextImage()
     * showMap()
     * selectImageSet()
     * etc
     */
}
