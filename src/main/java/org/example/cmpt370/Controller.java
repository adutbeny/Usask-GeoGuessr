package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/** Class that handles all user inputs and delegates it
 * to whatever the model needs to do to process it */
public class Controller {

    Model model;

    public Controller() {}

    /** Attach Model */
    public void setModel(Model m) {
        this.model = m;
    }

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
