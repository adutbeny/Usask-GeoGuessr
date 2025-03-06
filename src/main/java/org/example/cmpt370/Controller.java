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
            this.model.showLoginWindow();
        });
        view.createAcc.setOnAction(event -> {
            this.model.showCreateAccWindow();
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
        view.back1.setOnAction(event -> {
            this.model.showStartupWindow();
        });
        view.submitLogin.setOnAction(event -> {
            String username = view.usernameField.getText();
            String password = view.passwordField.getText();

            System.out.println("Login attempted with username: " + username + ", password: " + password);
            this.model.verifyLogin(username, password);
            // TODO: implement this method in model
            // TODO: need to add handling for incorrect password
        });
        view.submitCreate.setOnAction(event -> {
            String username = view.usernameCreate.getText();
            String password = view.passwordCreate.getText();

            System.out.println("Login attempted with username: " + username + ", password: " + password);
            this.model.createAccount(username, password);
            // TODO: implement this is model
        });
        view.submit.setOnAction(event -> {
            this.model.getDistance();
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
