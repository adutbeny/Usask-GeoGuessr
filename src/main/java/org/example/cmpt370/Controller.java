package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/** Class that handles all user inputs and delegates it
 * to whatever the model needs to do to process it */
public class Controller {

    Model model;

    /** Controller Constructor
     * @param view takes view as a argument to set up handling
     *             for all the buttons, without a permanent
     *             connection which would break the MVC architecture
     */
    public Controller(View view) {
        view.quickplay.setOnAction(event -> {
            this.model.showDifficultyWindow();
            // TODO: will need to disable leaderboards/high-score if
            // this becomes like an "offline" mode
        });
        view.login.setOnAction(event -> {
            // TODO: attach accounts here
            this.model.showDifficultyWindow();
        });
        view.createAcc.setOnAction(event -> {
            // TODO: attach accounts here
            this.model.showDifficultyWindow();
        });
        view.easy.setOnAction(event -> {
            this.model.selectPictureSet("/BeginnerPhotos.csv");
        });
        view.medium.setOnAction(event -> {
            this.model.selectPictureSet("/MediumPictures.csv");
        });
        view.hard.setOnAction(event -> {
            this.model.selectPictureSet("/HardPictures.csv");
        });
        view.submit.setOnAction(event -> {
            System.out.println("you Suck");
            // get the current picture from model
            Picture currentPicture = model.getCurrentPicture();
            // make sure it exists
            if (currentPicture == null) {
                System.out.println("No picture loaded.");
                return;
            }

            // get the marker coordinates from model
            JavaConnector connector = model.getJavaConnector();
            if (connector == null) {
                System.out.println("Marker coordinates not set.");
                return;
            }

            // get pictures longitude and latitude, print statements for debugging
            // TODO: the cords are not alligning at all we need to fix it somehow
            double pictureLat = currentPicture.getLatitude();
            System.out.println("picture latitude:" + pictureLat);
            double pictureLng = currentPicture.getLongitude();
            System.out.println("picture longitude:" + pictureLng);


            // Gets marker coordinates from connector
            double markerLat = connector.getMarkerLat();
            System.out.println("marker latitude:" + markerLat);
            double markerLng = connector.getMarkerLng();
            System.out.println("marker longitude:" + markerLng);

            // find distance between and print to console for now
            double distance = Model.haversine(pictureLat, pictureLng, markerLat, markerLng);
            System.out.println("You got: " + distance + " meters away!");
        });
    }

    /** Attach Model */
    public void setModel(Model m) {
        this.model = m;
    }


    // All of these will probably end up being unused...
    /** Handling method for when the mouse is pressed
     * @param e the mouse event */
    public void handlePressed(MouseEvent e) {}

    /** Handling method for mouse being dragged, may not be needed
     * @param e mouse event */
    public void handleDragged(MouseEvent e) {}

    /** Handling method that tracks to location of the mouse
     * Good for interactive feedback
     * @param e mouse event */
    public void handleMouseMoved(MouseEvent e) {}

    /** Handling method for when the mouse is released
     * @param e mouse event */
    public void handleReleased(MouseEvent e) {}

    /** Handling method for keyboard input
     * Might not need this one either
     * @param k the key pressed (use k.getKeyCode == [KEY]) */
    public void handleKeyPressed(KeyEvent k) {}
}
