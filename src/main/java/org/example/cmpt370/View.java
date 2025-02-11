package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import java.util.*;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/** Class that handles all display output
 * Needs to be updated by the Model each time
 * the user does something that requires the screen
 * to change (display new picture, map, etc.) */
public class View extends StackPane implements Subscriber {

    private Model model;
    private Canvas myCanvas;
    private GraphicsContext gc;

    /** Constructor -
     * Runs all start up to create initial display when program starts */
    public View() {
        this.myCanvas = new Canvas(1200,800);
        this.gc = myCanvas.getGraphicsContext2D();

        // background
        Image background = new Image("map2.jpeg");
        ImageView bv = new ImageView(background);
        // Set the image to fill the window
        bv.setFitWidth(1200);
        bv.setFitHeight(800);
        bv.setPreserveRatio(true);

        // adds semi-transparent backing
        gc.setFill(new Color(1, 1, 1, 0.5));
        gc.fillRect(200, 0, 400, 800);

        // logo
        Image l = new Image("usaskcrest.png");
        ImageView logo = new ImageView(l);
        logo.setFitHeight(150);
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);
        logo.setTranslateX(-200);
        logo.setTranslateY(-250);

        // title
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 38));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Usask GeoGuessr", 400, 250);

        // setup buttons
        Button quickplay = new Button("Quickplay");
        quickplay.setPrefWidth(200);
        Button login = new Button("Log-In");
        login.setPrefWidth(200);
        Button createAcc = new Button("Create Account");
        createAcc.setPrefWidth(200);

        VBox buttonStack = new VBox(25, quickplay, login, createAcc);
        // set below text
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // add all to layout in order!!!
        this.getChildren().addAll(bv, myCanvas, logo, buttonStack);
    }

    public void selectDifficultyWindow() {
        // clear any objects in view
        this.getChildren().setAll();
        // reset canvas
        gc.clearRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        // background
        Image background = new Image("map2.jpeg");
        ImageView bv = new ImageView(background);
        // Set the image to fill the window
        bv.setFitWidth(1200);
        bv.setFitHeight(800);
        bv.setPreserveRatio(true);

        // adds semi-transparent backing
        gc.setFill(new Color(1, 1, 1, 0.5));
        gc.fillRect(200, 0, 400, 800);

        // logo
        Image l = new Image("usaskcrest.png");
        ImageView logo = new ImageView(l);
        logo.setFitHeight(150);
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);
        logo.setTranslateX(-200);
        logo.setTranslateY(-250);

        // title
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 38));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Select Difficulty", 400, 250);

        // setup buttons
        Button quickplay = new Button("Novice Navigator");
        quickplay.setPrefWidth(200);
        Button login = new Button("Seasonal Searcher");
        login.setPrefWidth(200);
        Button createAcc = new Button("Expert Explorer");
        createAcc.setPrefWidth(200);

        VBox buttonStack = new VBox(25, quickplay, login, createAcc);
        // set below text
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // add all to layout in order!!!
        this.getChildren().addAll(bv, myCanvas, logo, buttonStack);
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
