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
            this.model.setMultiplayerMode(false);
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
            if (!this.model.isMultiplayerMode()) {
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
            } else {
                System.out.println("multiplayer is working");
                double p1_dist = model.calculateMultiplayerScore();
                double[] oppGuess = model.getOpponentGuess();

                JavaConnector connector = model.getJavaConnector();
                double p1_markerLat = connector.getMarkerLat();
                double p1_markerLng = connector.getMarkerLng();
                Picture curr = model.getCurrentPicture();
                double pictureLat = curr.getLatitude();
                double pictureLng = curr.getLongitude();

                // this gets the opponents distance
                if (oppGuess == null) {
                    double p2_dist = 1000; // TODO need to make sure this score gets recorded somehow, only happens if opponent doesnt make a guess
                    view.updateMapOverlay(p1_markerLat, p1_markerLng, pictureLat, pictureLng, p1_dist);
                } else {
                    double p2_dist = model.haversine(pictureLat, pictureLng, oppGuess[0], oppGuess[1]);
                    view.multiplayerUpdateMapOverlay(p1_markerLat, p1_markerLng, oppGuess[0], oppGuess[1], pictureLat, pictureLng, p1_dist, p2_dist);

                }

                view.submit.setVisible(false);
                view.next.setVisible(true);

            }
        });
        view.next.setOnAction(event -> {
            this.model.clearMultiplayerGuesses();
            this.model.loadNextRound();
            view.next.setVisible(false);
            view.submit.setVisible(true);
        });
        view.addChat.setOnAction(event -> {
            this.model.toggleChatVisibility();
        });

        view.multiplayer.setOnAction(event ->{
            this.model.showMatchmakingWindow();
            this.model.startMatchmaking();
        });

        view.addPin.setOnAction(event -> {
            this.model.pin();
        });
        view.unpin.setOnAction(event -> {
            this.model.unpin(this.model.getUser().getUsername(), this.model.getCurrentPicture().getPath());
        });
        view.playAgain.setOnAction(event -> {
            this.model.showDifficultyWindow();
        });
        // kills program
        view.exit.setOnAction(event -> {
            if (this.model.chatScheduler != null) {
                this.model.chatScheduler.shutdown();
            }
            this.model.exitGame();
        });
    }
}
