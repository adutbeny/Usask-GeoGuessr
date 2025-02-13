package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

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

import java.util.Objects;

/** Class that handles all display output
 * Needs to be updated by the Model each time
 * the user does something that requires the screen
 * to change (display new picture, map, etc.) */
public class View extends StackPane implements Subscriber {

    private Model model;
    private Canvas myCanvas;
    private GraphicsContext gc;
    // Buttons (so that the controller can set handlers)
    public Button quickplay;
    public Button login;
    public Button createAcc;
    public Button easy;
    public Button medium;
    public Button hard;

    /** Constructor -
     * Runs all start up to create initial display when program starts */
    public View() {
        this.myCanvas = new Canvas(1200,800);
        this.gc = this.myCanvas.getGraphicsContext2D();

        // setup buttons
        // for this window
        this.quickplay = new Button("Quickplay");
        this.quickplay.setPrefWidth(200);
        this.login = new Button("Log-In");
        this.login.setPrefWidth(200);
        this.createAcc = new Button("Create Account");
        this.createAcc.setPrefWidth(200);
        // buttons for later windows
        this.easy = new Button("Novice Navigator");
        this.easy.setPrefWidth(200);
        this.medium = new Button("Seasonal Searcher");
        this.medium.setPrefWidth(200);
        this.hard = new Button("Expert Explorer");
        this.hard.setPrefWidth(200);
        // TODO: add future buttons here

        selectMainMenu();
    }

    /** Method to show main start up screen
     * Brought this out of the constructor so we can go back to it if needed
     */
    public void selectMainMenu() {
        // background
        // completely idiotic but this is how you have to load an image
        Image background = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/map2.jpeg")).toExternalForm());
        ImageView bg = new ImageView(background);
        // Set the image to fill the window
        bg.setFitWidth(1200);
        bg.setFitHeight(800);
        bg.setPreserveRatio(true);

        // adds semi-transparent backing
        this.gc.setFill(new Color(1, 1, 1, 0.5));
        this.gc.fillRect(200, 0, 400, 800);

        // logo
        Image l = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/usaskcrest.png")).toExternalForm());
        ImageView logo = new ImageView(l);
        logo.setFitHeight(150);
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);
        logo.setTranslateX(-200);
        logo.setTranslateY(-250);

        // title
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Courier Prime", 38));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Usask GeoGuessr", 400, 270);
        VBox buttonStack = new VBox(25, this.quickplay, this.login, this.createAcc);
        // set below text
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // add all to layout in order!!!
        this.getChildren().addAll(bg, this.myCanvas, logo, buttonStack);
    }

    /** Shows window with buttons to select difficulty
     * Same other components as startup */
    public void selectDifficultyWindow() {
        // clear any objects in view
        this.getChildren().setAll();
        // reset canvas
        gc.clearRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        // background
        Image background = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/map2.jpeg")).toExternalForm());
        ImageView bv = new ImageView(background);
        // Set the image to fill the window
        bv.setFitWidth(1200);
        bv.setFitHeight(800);
        bv.setPreserveRatio(true);

        // adds semi-transparent backing
        gc.setFill(new Color(1, 1, 1, 0.5));
        gc.fillRect(200, 0, 400, 800);

        // logo
        Image l = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/usaskcrest.png")).toExternalForm());
        ImageView logo = new ImageView(l);
        logo.setFitHeight(150);
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);
        logo.setTranslateX(-200);
        logo.setTranslateY(-250);

        // title
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Courier Prime", 38));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Select Difficulty", 400, 270);

        VBox buttonStack = new VBox(25, this.easy, this.medium, this.hard);
        // set below text
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // add all to layout in order!!!
        this.getChildren().addAll(bv, myCanvas, logo, buttonStack);
    }

    /** Displays window that will be used during main playing area runtime */
    public void selectGameplayWindow() {
        // clear any objects in view
        this.getChildren().setAll();
        // reset canvas
        this.gc.clearRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        // dark green background
        this.gc.setFill(Color.DARKGREEN);
        this.gc.fillRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        // outlines for username, score
        this.gc.setFill(Color.BLACK);
        this.gc.fillRoundRect(100, 50, 200, 50, 20, 20);
        this.gc.fillRoundRect(350, 50, 200, 50, 20, 20);

        // Draw Labels for Buttons
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(new Font("Courier Prime", 20));
        this.gc.fillText("Username", 200, 80);
        //this.gc.fillText(String.valueOf(this.model.getUsername()), 200, 80);
        this.gc.fillText("POINTS/LEVEL", 450, 80);
        //gc.fillText(String.valueOf(this.model.getScore()), 450, 80);
        this.gc.fillText("Round: " + this.model.getRound() + "/5", 1120, 75);

        // Draw Photo Area
        this.gc.setFill(Color.BLACK);
        this.gc.fillRect(150, 200, 600, 400);
        // TODO: change this with actual photo from model

        // Draw Map Area
        this.gc.setFill(Color.web("#1a1a1a")); // Dark gray (not pure black)
        this.gc.fillRect(800, 250, 300, 300);
        // TODO: replace this with connection to actual map API

        this.getChildren().add(this.myCanvas);
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


    /** Gets called when the model signals a change,
     * then based on what state the model is in, show/update
     * the appropriate window */
    @Override
    public void modelUpdated() {
        // call needed methods based on what changed
        switch (this.model.getCurrentWindow()) {
            case STARTUP -> selectMainMenu();
            case DIFF -> selectDifficultyWindow();
            case GAMEPLAY -> selectGameplayWindow();
        }
    }
}
