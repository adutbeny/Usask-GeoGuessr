package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import netscape.javascript.JSObject;

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
    // main menu
    public Button quickplay;
    public Button login;
    public Button createAcc;
    // select diff
    public Button easy;
    public Button medium;
    public Button hard;

    public Button back1;        // returns to startup window

    // login
    public TextField usernameField;
    public PasswordField passwordField;
    public Button submitLogin;
    // create account
    public TextField usernameCreate;
    public PasswordField passwordCreate;
    public Button submitCreate;

    // another back button if we need to return to a different window
    public Button back2;
    // gameplay loop
    public Button submit;

    /** Resets the View back to a blank slate, or 'tabula rasa'
     * Call at the start of every window creation method */
    public void resetView() {
        this.getChildren().clear();
        this.myCanvas = new Canvas(1200, 800);
        this.gc = this.myCanvas.getGraphicsContext2D();
    }

    /** Constructor -
     * Runs all start up to create initial display when program starts */
    public View() {
        this.myCanvas = new Canvas(1200, 800);
        this.gc = this.myCanvas.getGraphicsContext2D();

        // Setup Buttons

        // Base button style
        String buttonStyle = "-fx-background-color: linear-gradient(to bottom, #0A6A42, #084A2E); " + // Base color and darker shade
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10; " +
                "-fx-border-color: #06321E; " + // Darker border color
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 1); " + // Shadow
                "-fx-cursor: hand;"; // Hand cursor on hover

        // Hover style
        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #0C7A4F, #0A6A42); " + // Lighter gradient on hover
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 8, 0, 0, 2);"; // Increased shadow on hover

        // Pressed style
        String pressedStyle = "-fx-background-color: linear-gradient(to bottom, #084A2E, #0A6A42); " + // Darker gradient on press
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);"; // Decreased shadow on press

        // Main Window
        this.quickplay = new Button("Quickplay");
        this.quickplay.setStyle(buttonStyle);
        this.quickplay.setOnMouseEntered(e -> this.quickplay.setStyle(hoverStyle));
        this.quickplay.setOnMouseExited(e -> this.quickplay.setStyle(buttonStyle));
        this.quickplay.setOnMousePressed(e -> this.quickplay.setStyle(pressedStyle));
        this.quickplay.setOnMouseReleased(e -> this.quickplay.setStyle(hoverStyle));
        this.quickplay.setPrefWidth(200);

        this.login = new Button("Log-In");
        this.login.setStyle(buttonStyle);
        this.login.setOnMouseEntered(e -> this.login.setStyle(hoverStyle));
        this.login.setOnMouseExited(e -> this.login.setStyle(buttonStyle));
        this.login.setOnMousePressed(e -> this.login.setStyle(pressedStyle));
        this.login.setOnMouseReleased(e -> this.login.setStyle(hoverStyle));
        this.login.setPrefWidth(200);

        this.createAcc = new Button("Create Account");
        this.createAcc.setStyle(buttonStyle);
        this.createAcc.setOnMouseEntered(e -> this.createAcc.setStyle(hoverStyle));
        this.createAcc.setOnMouseExited(e -> this.createAcc.setStyle(buttonStyle));
        this.createAcc.setOnMousePressed(e -> this.createAcc.setStyle(pressedStyle));
        this.createAcc.setOnMouseReleased(e -> this.createAcc.setStyle(hoverStyle));
        this.createAcc.setPrefWidth(200);

        // Level Selector
        this.easy = new Button("Novice Navigator");
        this.easy.setStyle(buttonStyle);
        this.easy.setOnMouseEntered(e -> this.easy.setStyle(hoverStyle));
        this.easy.setOnMouseExited(e -> this.easy.setStyle(buttonStyle));
        this.easy.setOnMousePressed(e -> this.easy.setStyle(pressedStyle));
        this.easy.setOnMouseReleased(e -> this.easy.setStyle(hoverStyle));
        this.easy.setPrefWidth(200);

        this.medium = new Button("Seasonal Searcher");
        this.medium.setStyle(buttonStyle);
        this.medium.setOnMouseEntered(e -> this.medium.setStyle(hoverStyle));
        this.medium.setOnMouseExited(e -> this.medium.setStyle(buttonStyle));
        this.medium.setOnMousePressed(e -> this.medium.setStyle(pressedStyle));
        this.medium.setOnMouseReleased(e -> this.medium.setStyle(hoverStyle));
        this.medium.setPrefWidth(200);

        this.hard = new Button("Expert Explorer");
        this.hard.setStyle(buttonStyle);
        this.hard.setOnMouseEntered(e -> this.hard.setStyle(hoverStyle));
        this.hard.setOnMouseExited(e -> this.hard.setStyle(buttonStyle));
        this.hard.setOnMousePressed(e -> this.hard.setStyle(pressedStyle));
        this.hard.setOnMouseReleased(e -> this.hard.setStyle(hoverStyle));
        this.hard.setPrefWidth(200);

        // Button for submitting
        this.submit = new Button("Submit");
        this.submit.setStyle(buttonStyle);
        this.submit.setOnMouseEntered(e -> this.submit.setStyle(hoverStyle));
        this.submit.setOnMouseExited(e -> this.submit.setStyle(buttonStyle));
        this.submit.setOnMousePressed(e -> this.submit.setStyle(pressedStyle));
        this.submit.setOnMouseReleased(e -> this.submit.setStyle(hoverStyle));
        this.submit.setPrefWidth(200);

        // Back button - returns to startup
        this.back1 = new Button("Back");
        this.back1.setStyle(buttonStyle);
        this.back1.setOnMouseEntered(e -> this.back1.setStyle(hoverStyle));
        this.back1.setOnMouseExited(e -> this.back1.setStyle(buttonStyle));
        this.back1.setOnMousePressed(e -> this.back1.setStyle(pressedStyle));
        this.back1.setOnMouseReleased(e -> this.back1.setStyle(hoverStyle));
        this.back1.setPrefWidth(200);

        // submits credentials from login
        this.submitLogin = new Button("Login");
        this.submitLogin.setStyle(buttonStyle);
        this.submitLogin.setOnMouseEntered(e -> this.submitLogin.setStyle(hoverStyle));
        this.submitLogin.setOnMouseExited(e -> this.submitLogin.setStyle(buttonStyle));
        this.submitLogin.setOnMousePressed(e -> this.submitLogin.setStyle(pressedStyle));
        this.submitLogin.setOnMouseReleased(e -> this.submitLogin.setStyle(hoverStyle));
        this.submitLogin.setPrefWidth(200);

        // Text fields for login
        this.usernameField = new TextField();
        this.passwordField = new PasswordField();

        // submit button to create account
        this.submitCreate = new Button("Submit");
        this.submitCreate.setStyle(buttonStyle);
        this.submitCreate.setOnMouseEntered(e -> this.submitCreate.setStyle(hoverStyle));
        this.submitCreate.setOnMouseExited(e -> this.submitCreate.setStyle(buttonStyle));
        this.submitCreate.setOnMousePressed(e -> this.submitCreate.setStyle(pressedStyle));
        this.submitCreate.setOnMouseReleased(e -> this.submitCreate.setStyle(hoverStyle));
        this.submitCreate.setPrefWidth(200);

        // Text fields for creation
        this.usernameCreate = new TextField();
        this.passwordCreate = new PasswordField();

        // Another back button in case we need it
        this.back2 = new Button("Back");
        this.back2.setStyle(buttonStyle);
        this.back2.setOnMouseEntered(e -> this.back2.setStyle(hoverStyle));
        this.back2.setOnMouseExited(e -> this.back2.setStyle(buttonStyle));
        this.back2.setOnMousePressed(e -> this.back2.setStyle(pressedStyle));
        this.back2.setOnMouseReleased(e -> this.back2.setStyle(hoverStyle));
        this.back2.setPrefWidth(200);

        // TODO: Add future buttons here
    }

    /** Method to show main start up screen
     * Brought this out of the constructor so we can go back to it if needed
     */
    public void selectMainMenu() {
        this.resetView();

        // TODO: see if we can move some of this into a method to share with the
        //  difficulty window so that we only have to make changes to one place?
        // background
        // completely idiotic but this is how you have to load an image
        Image background = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/betterfiller.jpeg")).toExternalForm());
        ImageView bg = new ImageView(background);

        // Set the image to fill the window
        bg.setFitWidth(1200);
        bg.setFitHeight(800);
        bg.setPreserveRatio(true);

        // Adds semi-transparent backing
        this.gc.setFill(new Color(1, 1, 1, 0.5));
        this.gc.fillRect(200, 0, 400, 800);

        // Usask Logo
        Image l = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/usaskcrest.png")).toExternalForm());
        ImageView logo = new ImageView(l);
        logo.setFitHeight(150);
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);
        logo.setTranslateX(-200);
        logo.setTranslateY(-250);

        // Title
        this.gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 39));
        this.gc.setTextAlign(TextAlignment.CENTER);

        // Create a linear gradient for the text fill
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(1, Color.rgb(20, 150, 100))
        );

        // Add a drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        this.gc.setEffect(dropShadow);

        // Draw the black outline
        this.gc.setFill(Color.BLACK);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                gc.fillText("Usask GeoGuessr", 400 + i, 270 + j);
            }
        }

        //  Main Text
        this.gc.setFill(gradient);
        this.gc.fillText("Usask GeoGuessr", 400, 270);

        VBox buttonStack = new VBox(25, this.quickplay);
        // if we have internet connection, display login buttons
        if (this.model.getInternetStatus()) {
            buttonStack.getChildren().addAll(this.login, this.createAcc);
        } else {
            // no internet, display text message indicating this
            Text noInternet = new Text("Internet unavailable, playing offline...");
            // TODO - need to check this and see if it looks good
            buttonStack.getChildren().add(noInternet);
        }
        // set below text
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // add all to layout in order!!!
        this.getChildren().addAll(bg, this.myCanvas, logo, buttonStack);
    }


    /** Shows window with buttons to select difficulty
     * Same other components as startup */
    public void selectDifficultyWindow() {
        this.resetView();

        // Background
        Image background = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/betterfiller.jpeg")).toExternalForm());
        ImageView bv = new ImageView(background);

        bv.setFitWidth(1200);
        bv.setFitHeight(800);
        bv.setPreserveRatio(true);

        // Semi-transparent backing
        this.gc.setFill(new Color(1, 1, 1, 0.5));
        this.gc.fillRoundRect(200, 0, 400, 800, 20, 20);

        // Create a DropShadow effect for the backing
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.BLACK);
        this.gc.setEffect(dropShadow);

        // Usask Logo
        Image l = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/usaskcrest.png")).toExternalForm());
        ImageView logo = new ImageView(l);
        logo.setFitHeight(150);
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);
        logo.setTranslateX(-200);
        logo.setTranslateY(-250);

        // Title
        this.gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 39));
        this.gc.setTextAlign(TextAlignment.CENTER);

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(1, Color.rgb(20, 150, 100))
        );

        // black outline
        this.gc.setFill(Color.BLACK);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                this.gc.fillText("Select Difficulty", 400 + i, 270 + j);
            }
        }

        // Main text
        this.gc.setFill(gradient);
        this.gc.fillText("Select Difficulty", 400, 270);

        // Button Stack
        VBox buttonStack = new VBox(25, this.easy, this.medium, this.hard, this.back1);
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // Add all to layout
        this.getChildren().addAll(bv, this.myCanvas, logo, buttonStack);
    }


    /** Displays window that will be used during main playing area runtime */
    public void selectGameplayWindow() {
        this.resetView();

        // Usask Green
        this.gc.setFill(Color.rgb(10, 106, 66));
        this.gc.fillRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        // Outlines for username, score
        this.gc.setFill(Color.BLACK);
        this.gc.fillRoundRect(100, 50, 200, 50, 20, 20);
        this.gc.fillRoundRect(350, 50, 200, 50, 20, 20);

        // Draw Labels for Buttons
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(new Font("Courier Prime", 20));

        if (!this.model.getInternetStatus() || this.model.getUser() == null) {
            this.gc.fillText("Quickplay", 200, 80);
        } else {
            this.gc.fillText("Username", 200, 80);
            //this.gc.fillText(String.valueOf(this.model.getUsername()), 200, 80);
        }
        this.gc.fillText("Points " + this.model.getTotalScore(), 450, 80);

        this.gc.fillText("Round: " + this.model.getRound() + "/5", 1050, 75);

        // Draw Photo Area
        this.gc.setFill(Color.BLACK);
        this.gc.fillRoundRect(150, 200, 600, 400, 20, 20);
        Picture curr = this.model.getCurrentPicture();
        ImageView c = null;
        if (curr == null) {
            this.gc.setFill(Color.WHITE);
            this.gc.fillText("No pictures loaded", 450, 400);
        } else {
            Image current = new Image(Objects.requireNonNull(
                            getClass().getResource(curr.getPath()))
                    .toExternalForm()
            );
            c = new ImageView(current);
            c.setPreserveRatio(true);
            c.setFitHeight(400);
            c.setFitWidth(600);
            c.setTranslateX(-150);
        }

        // Draw Map Area
        this.gc.setFill(Color.web("#1a1a1a")); // Dark gray (not pure black)
        this.gc.fillRect(800, 250, 300, 300);

        VBox buttonStack = new VBox(25, this.submit);
        // set below text
        buttonStack.setTranslateX(500);
        buttonStack.setTranslateY(725);

        Pane layout = new Pane();
        layout.setPrefSize(1200, 800);

        WebView mapView = new WebView();

        mapView.setPrefSize(400, 400);
        mapView.relocate(775, 200);

        // loads map api with html file
        WebEngine engine = mapView.getEngine();

        //this is checking for errors its not loading correctly
        engine.setOnError(event -> System.out.println("WebView Error: " + event.getMessage()));
        engine.setOnAlert(event -> System.out.println("WebView Alert: " + event.getData()));
        engine.setJavaScriptEnabled(true);

        // this is for connecting the html to our java so we can get the coords
        JavaConnector connector = new JavaConnector();
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("javaApp", connector);
                // for debugging
                System.out.println("JavaConnector set on JS window");
            }
        });

        this.model.setJavaConnector(connector); //store in model

        //load the map from the html file
        engine.load(Objects.requireNonNull(getClass().getResource("/public/map.html")).toExternalForm());

        // here we resize the map if the mouse hovers over it so we get a better view
        mapView.setOnMouseEntered(event -> {
            mapView.setPrefSize(this.myCanvas.getWidth(), this.myCanvas.getHeight() - 200);
            mapView.relocate(0, 100);
        });
        mapView.setOnMouseExited(event -> {
            mapView.setPrefSize(400, 400);
            mapView.relocate(775, 200);
        });

        layout.getChildren().add(mapView);
        this.getChildren().addAll(this.myCanvas, c, layout, buttonStack);
    }


    // TODO: these
    /** Displays fields to enter user information
     * Needs to connect to database to verify credentials
     * and then once verified should create User instance in model */
    public void loginWindow() {
        this.createSignInBackground();

        // Title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 36));
        this.gc.setTextAlign(TextAlignment.CENTER);
        this.gc.fillText("Login", 600, 250);

        // Username Field
        this.usernameField.setPromptText("Username");
        this.usernameField.setPrefWidth(300);
        this.usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10;");
        this.usernameField.setLayoutX(450);
        this.usernameField.setLayoutY(300);

        // Password Field
        this.passwordField.setPromptText("Password");
        this.passwordField.setPrefWidth(300);
        this.passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10;");
        this.passwordField.setLayoutX(450);
        this.passwordField.setLayoutY(350);

        this.back1.setLayoutX(500);
        this.back1.setLayoutY(500);

        this.submitLogin.setLayoutX(500);
        this.submitLogin.setLayoutY(420);

        // add to layout
        Pane layout = new Pane();
        layout.getChildren().addAll(this.usernameField, this.passwordField, this.submitLogin, this.back1);
        this.getChildren().addAll(this.myCanvas, layout);
    }

    /** Displays fields to enter user information
     * Needs to connect to database to create new entry in the DB
     * and then once verified should create User instance in model
     * overall should be pretty similar to login but with different handling */
    public void createAccWindow() {
        this.createSignInBackground();

        // Title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(Font.font("Arial Black", FontWeight.BOLD, 32));
        this.gc.setTextAlign(TextAlignment.CENTER);
        this.gc.fillText("Create Account", 600, 250);

        // Username Field
        this.usernameCreate.setPromptText("Username");
        this.usernameCreate.setPrefWidth(300);
        this.usernameCreate.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10;");
        this.usernameCreate.setLayoutX(450);
        this.usernameCreate.setLayoutY(300);

        // Password Field
        this.passwordCreate.setPromptText("Password");
        this.passwordCreate.setPrefWidth(300);
        this.passwordCreate.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10;");
        this.passwordCreate.setLayoutX(450);
        this.passwordCreate.setLayoutY(350);

        this.back1.setLayoutX(500);
        this.back1.setLayoutY(500);

        this.submitCreate.setLayoutX(500);
        this.submitCreate.setLayoutY(420);

        // add to layout
        Pane layout = new Pane();
        layout.getChildren().addAll(this.usernameCreate, this.passwordCreate, this.submitCreate, this.back1);
        this.getChildren().addAll(this.myCanvas, layout);
    }

    private void createSignInBackground() {
        this.resetView();

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)), // Start color (Usask green
                new Stop(1, Color.rgb(20, 150, 100))  // End color (lighter green)
        );
        this.gc.setFill(gradient);
        this.gc.fillRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        this.gc.setFill(new Color(1, 1, 1, 0.6));
        this.gc.fillRoundRect(400, 200, 400, 400, 20, 20);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.BLACK);
        this.gc.setEffect(dropShadow);
    }

    /** Connect Model */
    public void setModel(Model m) {
        this.model = m;
        selectMainMenu();
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
            case LOGIN -> loginWindow();
            case CREATE -> createAccWindow();
        }
    }
}
