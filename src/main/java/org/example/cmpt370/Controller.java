package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.application.Platform;

/** Class that handles all user inputs and delegates it
 * to whatever the model needs to do to process it */
public class Controller {

    Model model;

    /** Attach Model */
    public void setModel(Model m) {
        this.model = m;
    }

    /** Controller Constructor
     * @param view takes view as an argument to set up HANDLING
     *             for all buttons and other elements, without a permanent connection */
    public Controller(View view) {
        // Sends to difficulty select, no account
        view.quickplay.setOnAction(event -> {
            this.model.showDifficultyWindow();
            //TODO: will need to disable leaderboards/high-score if
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
            // load fields
            String username = view.usernameField.getText();
            String password = view.passwordField.getText();
            boolean rememberMe = view.RememberMe.isSelected();

            // Attempt log in based off whether or not user has remember me on
            if (rememberMe) {
                if (this.model.verifyLogin(username, password, rememberMe)) {
                    System.out.println("Login successful!");
                }
                else{
                    System.out.println("Login failed!");
                }
            }
            else {
                if (this.model.verifyLogin(username, password, false)) {
                    System.out.println("Login successful!");
                }
                else{
                    System.out.println("Login failed!");
                }
            }

            System.out.println("Login attempted with username: " + username + ", password: " + password);
            // TODO: need to add handling for incorrect password
        });
        // create a new account in database
        view.submitCreate.setOnAction(event -> {
            String username = view.usernameCreate.getText();
            String password = view.passwordCreate.getText();

            System.out.println("Login attempted with username: " + username + ", password: " + password);
            this.model.createAccount(username, password);
        });
        // cues google sign in
        view.googleSignIn.setOnAction(event -> {
            this.model.initiateGoogleSignIn();
        });
        // Available after successful log-in
        view.history.setOnAction(event -> {
            this.model.showHistoryWindow();
        });
        view.pinned.setOnAction(event -> {
            this.model.showPinnedWindow();
        });
        view.leaderboard.setOnAction(event -> {
            this.model.showLeaderboard();
        });
        // return to logged in menu
        view.back2.setOnAction(event -> this.model.showLoggedInWindow());
        view.menu.setOnAction(event -> {
            this.model.showLoggedInWindow();
        });
        // gameplay loop interactions
        view.submit.setOnAction(event -> {
            double dist = model.getDistance();
            /* need to have this in controller so that we can call updateMapOverlay()
             in view to send to our html, this is a slight workaround could probably find
             a different solution */
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
        view.next.setOnAction(event -> {
            this.model.loadNextRound();
            view.next.setVisible(false);
            view.submit.setVisible(true);
        });
        view.multiplayer.setOnAction(event ->{
            this.model.startMatchmaking();
        });
        view.addPin.setOnAction(event -> {
            this.model.pin();
        });
        view.unpin.setOnAction(event -> {
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
}
