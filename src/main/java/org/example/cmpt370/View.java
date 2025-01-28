package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

/** Class that handles all display output
 * Needs to be updated by the Model each time
 * the user does something that requires the screen
 * to change (display new picture, map, etc.) */
public class View extends StackPane implements Subscriber {

    private Model model;
    private Canvas myCanvas;

    public View() {
        this.myCanvas = new Canvas(1000,1000);
        this.getChildren().addAll(myCanvas);
    }

    /** Connect Model */
    public void setModel(Model m) {
        this.model = m;
    }

    /** Attaches itself to controller so that we can receive
     * interactions from the user and pass to the appropriate
     * @param controller method
     */
    public void setupEvents(Controller controller) {
        myCanvas.setOnMousePressed(controller::handlePressed);
        myCanvas.setOnMouseDragged(controller::handleDragged);
        myCanvas.setOnMouseReleased(controller::handleReleased);
        setOnKeyPressed(controller::handleKeyPressed);
        myCanvas.setOnMouseMoved(controller::handleMouseMoved);
    }


    @Override
    public void modelUpdated() {
        // call needed methods based on what changed
    }
}
