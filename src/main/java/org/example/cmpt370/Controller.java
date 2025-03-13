package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/** Class that handles all user inputs and delegates it
 * to whatever the model needs to do to process it */
public class Controller {

    Model model;

    /** Controller Constructor
     * @param view takes view as a argument to set up HANDLING
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
            this.model.showLoginWindow();
        });
        view.createAcc.setOnAction(event -> {
            this.model.showCreateAccWindow();
        });
        view.easy.setOnAction(event -> {
            this.model.selectPictureSet("/BeginnerPhotos.csv");
            this.model.setDifficulty("/BeginnerPhotos.csv");
        });
        view.medium.setOnAction(event -> {
            this.model.selectPictureSet("/MediumPictures.csv");
            this.model.setDifficulty("/MediumPictures.csv");
        });
        view.hard.setOnAction(event -> {
            this.model.selectPictureSet("/HardPictures.csv");
            this.model.setDifficulty("/HardPictures.csv");
        });
        view.back1.setOnAction(event -> {
            this.model.showStartupWindow();
        });
        // log in
        view.submitLogin.setOnAction(event -> {
            String username = view.usernameField.getText();
            String password = view.passwordField.getText();

            System.out.println("Login attempted with username: " + username + ", password: " + password);
            this.model.verifyLogin(username, password);
            // TODO: need to add handling for incorrect password
        });
        view.submitCreate.setOnAction(event -> {
            String username = view.usernameCreate.getText();
            String password = view.passwordCreate.getText();

            System.out.println("Login attempted with username: " + username + ", password: " + password);
            this.model.createAccount(username, password);
        });
        // logged in
        view.history.setOnAction(event -> {
            this.model.showHistoryWindow();
        });
        view.pinned.setOnAction(event -> {
            this.model.showPinnedWindow();
        });
        view.leaderboard.setOnAction(event -> {
            this.model.showLeaderboard();
        });
        view.multiplayer.setOnAction(event -> {
            // TODO: Figure out how this looks
            //this.model.initiateMultiplayer();
        });
        view.back2.setOnAction(event -> {
            this.model.showLoggedInWindow();
        });
        view.menu.setOnAction(e -> {
            this.model.showLoggedInWindow();
        });
        // gameplay
        view.submit.setOnAction(event -> {
            double dist = model.getDistance();
            /* need to have this in controller so that we can call updateMapOverlay()
             in view to send to our html, this is a slight workaround could probably find
             a different solution*/
            JavaConnector connector = model.getJavaConnector();
            double markerLat = connector.getMarkerLat();
            double markerLng = connector.getMarkerLng();
            Picture curr = model.getCurrentPicture();
            double pictureLat = curr.getLatitude();
            double pictureLng = curr.getLongitude();
            view.updateMapOverlay(markerLat, markerLng, pictureLat, pictureLng, dist);
            view.submit.setVisible(false);
            view.next.setVisible(true);
        });
        view.next.setOnAction(e -> {
            this.model.loadNextRound();
            view.next.setVisible(false);
            view.submit.setVisible(true);
        });
        view.addPin.setOnAction(e -> {
            this.model.pin();
        });
        view.unpin.setOnAction(e -> {
            this.model.unpin();
        });
        view.playAgain.setOnAction(event -> {
            this.model.showDifficultyWindow();
        });
        // kills program
        view.exit.setOnAction(event -> {
            Platform.exit();
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
