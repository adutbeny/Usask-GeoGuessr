package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/** Class that handles all display output
 * Needs to be updated by the Model each time
 * the user does something that requires the screen
 * to change (display new picture, map, etc.) */
public class View extends StackPane implements Subscriber {

    private Model model;
    private WebView mapView;
    private WebEngine mapEngine;
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
    public Button googleSignIn; // Button for Google Sign-In

    // create account
    public TextField usernameCreate;
    public PasswordField passwordCreate;
    public Button submitCreate;
    // end screen
    public Button playAgain;
    public Button exit;
    public Button menu;

    // gameplay loop
    public Button submit;
    public Button next;
    public Button addPin;
    public Button unpin;

    // logged in
    public Button history;
    public Button pinned;
    public Button leaderboard;
    public Button multiplayer;
    public Button back2;        // returns to logged in window

    // Need these to implement google sign in
    private WebView googleWebView;
    private WebEngine googleWebEngine;

    public Process pythonServerProcess;
    public boolean isPythonServerStarted = false;
    public GoogleAuthHandler googleAuthHandler;

    public CheckBox RememberMe;

    //LeaderBoard
    public Button myHighScore;
    public Button top16;
    public Button Novice;
    public Button Seasonal;
    public Button Expert;
    public Button back3;
    private Pane contentPane;
    private boolean showMyhighscore = false;
    private boolean showTop16 = false;
    private int selectedRound;


    /**
     * Resets the View back to a blank slate, or 'tabula rasa'
     * Call at the start of every window creation method
     */
    public void resetView() {
        this.getChildren().clear();
        this.myCanvas = new Canvas(1200, 800);
        this.gc = this.myCanvas.getGraphicsContext2D();
    }

    /**
     * Constructor -
     * Runs all start up to create initial display when program starts
     */
    public View() {
        this.myCanvas = new Canvas(1200, 800);
        this.gc = this.myCanvas.getGraphicsContext2D();
        this.googleAuthHandler = new GoogleAuthHandler();
        // Setup Buttons
        // Main Window
        this.quickplay = new Button("Quickplay");
        this.quickplay.setPrefWidth(200);

        this.login = new Button("Log-In");
        this.login.setPrefWidth(200);

        this.createAcc = new Button("Create Account");
        this.createAcc.setPrefWidth(200);

        // CreateAcc Button
        this.createAcc = new Button("Create Account");
        this.createAcc.setPrefWidth(200);

        // Level Selector
        this.easy = new Button("Novice Navigator");
        this.easy.setPrefWidth(200);

        this.medium = new Button("Seasonal Searcher");
        this.medium.setPrefWidth(200);

        this.hard = new Button("Expert Explorer");
        this.hard.setPrefWidth(200);

        // Button for submitting
        this.submit = new Button("Submit");
        this.submit.setPrefWidth(200);

        // Button for going to next round
        this.next = new Button("Next");
        this.next.setPrefWidth(200);

        // pin current image
        this.addPin = new Button("Pin");
        this.addPin.setPrefWidth(100);

        // unpin from the Pinned window
        this.unpin = new Button("Unpin");
        this.unpin.setPrefWidth(100);

        // Back button - returns to startup
        this.back1 = new Button("Back");
        this.back1.setPrefWidth(200);

        // submits credentials from login
        this.submitLogin = new Button("Login");
        this.submitLogin.setPrefWidth(200);

        // Text fields for login
        this.usernameField = new TextField();
        this.passwordField = new PasswordField();

        // submit button to create account
        this.submitCreate = new Button("Submit");
        this.submitCreate.setPrefWidth(200);

        // Text fields for creation
        this.usernameCreate = new TextField();
        this.passwordCreate = new PasswordField();

        // returns to select difficulty after game ends
        this.playAgain = new Button("Play Again");
        this.playAgain.setPrefWidth(200);

        // kill program
        this.exit = new Button("Exit");
        this.exit.setPrefWidth(200);

        // Another back button in case we need it
        this.back2 = new Button("Back");
        this.back2.setPrefWidth(200);
        this.menu = new Button("Menu"); // does the same thing
        this.menu.setPrefWidth(200);

        this.history = new Button("History");
        this.history.setPrefWidth(200);

        this.pinned = new Button("Pinned");
        this.pinned.setPrefWidth(200);

        this.leaderboard = new Button("Leaderboard");
        this.leaderboard.setPrefWidth(200);

        this.multiplayer = new Button("Multiplayer");
        this.multiplayer.setPrefWidth(200);

        // Google button stuff...
        this.googleSignIn = new Button("Sign in with Google");
        this.googleSignIn.setPrefWidth(200);

        // REMEMBER ME BUTTON
        this.RememberMe = new CheckBox("Remember Me?");
        this.RememberMe.setPrefWidth(400);

        this.myHighScore = new Button("My Scores");
        this.myHighScore.setPrefWidth(200);

        this.top16 = new Button("Top 16");
        this.top16.setPrefWidth(200);
        this.Novice = new Button("Novice");
        this.Novice.setPrefWidth(200);
        this.Seasonal = new Button("Seasonal");
        this.Seasonal.setPrefWidth(200);
        this.Expert = new Button("Expert");
        this.Expert.setPrefWidth(200);
        this.back3 = new Button("Back");
        this.back3.setPrefWidth(200);

        // TODO: Add future buttons here
    }

    /**
     * Method to show main start up screen
     * Brought this out of the constructor so we can go back to it if needed
     */
    public void selectMainMenu() {
        this.createDefaultBackground();

        // Create a linear gradient for the text fill
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(1, Color.rgb(20, 150, 100))
        );

        // Draw the black outline
        this.gc.setFill(Color.BLACK);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                gc.fillText("Usask GeoGuessr", 400 + i, 270 + j);
            }
        }

        // Main Text
        this.gc.setFill(gradient);
        this.gc.fillText("Usask GeoGuessr", 400, 270);

        VBox buttonStack = new VBox(25, this.quickplay);
        // If we have internet connection, display login buttons
        if (this.model.getInternetStatus()) {
            buttonStack.getChildren().addAll(this.login, this.createAcc);
        } else {
            // No internet, display text message indicating this
            Text noInternet = new Text("Internet unavailable, playing offline...");
            // TODO - need to check this and see if it looks good
            buttonStack.getChildren().add(noInternet);
        }
        buttonStack.getChildren().add(this.exit);
        // Set below text
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(360);

        // aAd all to layout in order!!!
        this.getChildren().add(buttonStack);
    }

    /**
     * Shows window with buttons to select difficulty
     * Same other components as startup
     */
    public void selectDifficultyWindow() {
        this.createDefaultBackground();

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(1, Color.rgb(20, 150, 100))
        );

        // Black outline
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
        this.getChildren().addAll(buttonStack);
    }

    /**
     * Creates default background and adds it to the View
     * used by main menu, select difficulty and others
     */
    private void createDefaultBackground() {
        this.resetView();

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

        // Add a drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        this.gc.setEffect(dropShadow);

        this.getChildren().addAll(bg, this.myCanvas, logo);
    }

    /**
     * Displays window that will be used during main playing area runtime
     */
    public void selectGameplayWindow() {
        this.resetView();

        Pane layout = new Pane();
        layout.setPrefSize(1200, 800);

        // Usask Green
        this.gc.setFill(Color.WHITE);
        this.gc.fillRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        // Usask Logo with University Saskatchewan
        Image l = new Image(Objects.requireNonNull(getClass().getResource("/USaskOffical/usask_usask_colour.png")).toExternalForm());
        ImageView logo = new ImageView(l);
        logo.setFitHeight(250);
        logo.setFitWidth(250);
        logo.setPreserveRatio(true);
        logo.setTranslateX(20);
        logo.setTranslateY(45);

        // Gradient color for box
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(0.5, Color.rgb(20, 150, 100))
        );

        // Draw Labels for Buttons
        double textbX = 650;
        double textbY = 40;
        double textWidth = 600;
        double textHeight = 75;
        double skew = 50;
        this.gc.setFill(gradient);

        // Parallellogram shape
        double[] xP = {textbX, textbX + textWidth, textbX + textWidth - skew, textbX - skew};
        double[] yP = {textbY, textbY, textbY + textHeight, textbY + textHeight};
        this.gc.fillPolygon(xP, yP, 4);

        //Username, Score, Round Label
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(new Font("Courier Prime", 20));
        this.gc.fillText("Username", textbX + 30, textbY + 25);
        if (this.model.getUser() != null) {
            if (this.model.getCurrentDifficulty() == DIFFICULTY.NOVICE) {
                if (this.model.getRecentScore() > this.model.getUser().getNoviceHighscore()) {
                    this.gc.fillText("Score (New Best!)", textbX + 230, textbY + 25);
                }
            } else if (this.model.getCurrentDifficulty() == DIFFICULTY.SEASONAL) {
                if (this.model.getRecentScore() > this.model.getUser().getSeasonalHighscore()) {
                    this.gc.fillText("Score (New Best!)", textbX + 230, textbY + 25);
                }
            } else if (this.model.getCurrentDifficulty() == DIFFICULTY.EXPERT) {
                if (this.model.getRecentScore() > this.model.getUser().getExpertHighscore()) {
                    this.gc.fillText("Score (New Best!)", textbX + 230, textbY + 25);
                }
            } else {
                this.gc.fillText("Score", textbX + 230, textbY + 25);
            }
        } else {
            this.gc.fillText("Score", textbX + 230, textbY + 25);
        }
        this.gc.fillText("Round", textbX + 410, textbY + 25);

        //Input for Username, Score and Round
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(Font.font("Courier Prime", FontWeight.BOLD, 25));
        if (this.model.getUser() == null) {
            this.gc.fillText(String.valueOf("User"), textbX + 30, 105);
        } else {
            this.gc.fillText(String.valueOf(this.model.getUser().getUsername()), textbX + 30, 105);//Accounts for 10 char
        }

        this.gc.fillText(String.valueOf(this.model.getTotalScore()), textbX + 230, 105);

        this.gc.fillText(this.model.getRound() + "/5", textbX + 415, 105);

        //Green border between the photo and the green username box
        double borderX = 0;
        double borderY = 115;
        double borderWidth = 1200;
        double borderHeight = 30;
        this.gc.setFill(Color.rgb(20, 150, 100));
        this.gc.fillRect(borderX, borderY, borderWidth, borderHeight);

        Picture curr = this.model.getNextPic();
        ImageView c = null;
        if (curr == null) {
            this.gc.setFill(Color.WHITE);
            this.gc.fillText("No pictures loaded", 450, 400);
        } else {

            //////////// Maybe Issue //////////
            System.out.println(this.model.getCurrentPicture().getPath());
            Image current = new Image(Objects.requireNonNull(getClass()
                            .getResource(this.model.getCurrentPicture().getPath()))
                    .toExternalForm()

            );
            c = new ImageView(current);
            c.setPreserveRatio(false);
            c.setFitWidth(1200);
            c.setFitHeight(670);
            c.setTranslateX(0);
            c.setTranslateY(70);

        }

        submit.setVisible(true);
        next.setVisible(false);
        VBox buttonStack = new VBox(0, submit, next);
        // Use relocate() to position the buttonStack absolutely.
        buttonStack.setTranslateX(500);
        buttonStack.setTranslateY(725);
        submit.heightProperty().addListener((obs, oldVal, newVal) -> {
            next.setTranslateY(-newVal.doubleValue());
        });

        mapView = new WebView();

        mapView.setPrefSize(200, 150);
        mapView.relocate(950, 590);

        // Loads map api with html file
        mapEngine = mapView.getEngine();

        // This is checking for errors it's not loading correctly
        mapEngine.setOnError(event -> System.out.println("WebView Error: " + event.getMessage()));
        mapEngine.setOnAlert(event -> {
            String alertMessage = event.getData();
            System.out.println("WebView Alert:" + alertMessage);
        });
        mapEngine.setJavaScriptEnabled(true);

        // This is for connecting the html to our java so we can get the coords
        JavaConnector connector = new JavaConnector();
        mapEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) mapEngine.executeScript("window");
                window.setMember("javaApp", connector);
                // for debugging
                System.out.println("JavaConnector set on JS window");
            }
        });
        mapView.setOnMouseEntered(event -> {
            mapView.setPrefSize(this.myCanvas.getWidth(), this.myCanvas.getHeight() - 200);
            mapView.relocate(0, 135);
        });
        mapView.setOnMouseExited(event -> {
            mapView.setPrefSize(200, 150);
            mapView.relocate(950, 590);
        });


        this.model.setJavaConnector(connector); //store in model

        // Load the map from the html file
        mapEngine.load(Objects.requireNonNull(getClass().getResource("/public/map.html")).toExternalForm());

        // Map interactions
        mapView.setOnMousePressed(event -> {
            mapView.setUserData(new double[]{event.getSceneX(), event.getY(), mapView.getLayoutX(), mapView.getLayoutY()});
        });

        // TODO- maybe add swap to an unpin button?
        this.addPin.setTranslateX(-540);
        this.addPin.setTranslateY(345);

        layout.getChildren().add(0, c);
        layout.getChildren().add(this.mapView);
        layout.getChildren().add(logo);
        this.getChildren().addAll(this.myCanvas, c, layout, buttonStack, this.addPin);
    }

    /**
     * This sends needed info to our map HTML so it can create a line between two points to help visualze the distance
     */
    public void updateMapOverlay(double guessedLat, double guessedLng,
                                 double correctLat, double correctLng,
                                 double distance) {
        String script = String.format(
                "showResult(%f, %f, %f, %f, %f);",
                guessedLat, guessedLng, correctLat, correctLng, distance
        );
        mapEngine.executeScript(script);
    }

    /**
     * Displays fields to enter user information
     * Needs to connect to database to verify credentials
     * and then once verified should create User instance in model
     */
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
        this.usernameField.setLayoutY(275);

        // Password Field
        this.passwordField.setPromptText("Password");
        this.passwordField.setPrefWidth(300);
        this.passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10;");
        this.passwordField.setLayoutX(450);
        this.passwordField.setLayoutY(325);


        this.submitLogin.setLayoutX(500);
        this.submitLogin.setLayoutY(385);

        // Google sign in button...
        this.googleSignIn.setLayoutX(500);
        this.googleSignIn.setLayoutY(450);

        this.back1.setLayoutX(500);
        this.back1.setLayoutY(515);

        this.RememberMe.setLayoutX(520);
        this.RememberMe.setLayoutY(570);

        // TODO this has to be moved to i juist need focus on the logic rihgt now SORRY...
        String[] savedCredentials = model.loadCredentials();
        if (savedCredentials != null) {
            this.usernameField.setText(savedCredentials[0]);
            this.passwordField.setText(savedCredentials[1]);
            this.RememberMe.setSelected(true);
        }

        // Set action for the Login button

        // TODO this should be in controller if we can figure that out ( I AM VOLUTEENDING MATT BERRY TO DO THIS )
        // Set action for Google Sign-In button
        this.googleSignIn.setOnAction(event -> {
            this.googleAuthHandler.startPythonServer(); // Start the Python server
            this.googleAuthHandler.openGoogleSignInPage(); // Open the Google Sign-In page
        });

        // add to layout
        Pane layout = new Pane();
        layout.getChildren().addAll(this.usernameField, this.passwordField, this.submitLogin, this.googleSignIn, this.back1, this.RememberMe);
        this.getChildren().addAll(this.myCanvas, layout);

        this.googleAuthHandler.startTokenChecker(() -> {
            // Handle token received
            System.out.println("Token received, updating UI...");
        });

    }

    /**
     * Displays fields to enter user information
     * Needs to connect to database to create new entry in the DB
     * and then once verified should create User instance in model
     * overall should be pretty similar to login but with different handling
     */
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

        // Add to layout
        Pane layout = new Pane();
        layout.getChildren().addAll(this.usernameCreate, this.passwordCreate, this.submitCreate, this.back1);
        this.getChildren().addAll(this.myCanvas, layout);
    }

    /** View for when the user has successfully logged in */
    public void createLoggedInWindow() {
        createDefaultBackground();

        // Create a linear gradient for the text fill
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(1, Color.rgb(20, 150, 100))
        );

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

        // Button Stack
        VBox buttonStack = new VBox(20, this.quickplay, this.history, this.pinned, this.leaderboard, this.multiplayer, this.exit);
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(325);

        // Add all to layout
        this.getChildren().addAll(buttonStack);
    }

    /** Background for use with the log in and create account window */
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

    /**
     * Creates end screen to display score and option to play again, exit
     * Mostly probably actually used if playing offline and cant show leaderboard
     */
    private void createEndScreen() {
        System.out.println("CreateEndScreen call");
        this.resetView();

        // TODO: make this not temporary
        // green background
        this.gc.setFill(Color.rgb(10, 106, 66));
        this.gc.fillRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        Text current = new Text("Your Score: " + this.model.getTotalScore());
        current.setFont(new Font(30));
        boolean newBest = false;
        Text high = null;
        if (this.model.getUser() != null) {
            this.model.saveHistory(this.model.getTotalScore());
            if (this.model.getCurrentDifficulty() == DIFFICULTY.NOVICE){
                if (this.model.getTotalScore() > this.model.getUser().getNoviceHighscore()){
                    this.model.getUser().setN_highscore(this.model.getTotalScore());
                    this.model.adjustHighScore();
                    newBest = true;
                }
                high = new Text("High score: " + this.model.getUser().getNoviceHighscore());
            }
            else if (this.model.getCurrentDifficulty() == DIFFICULTY.SEASONAL){
                if (this.model.getTotalScore() > this.model.getUser().getSeasonalHighscore()){
                    this.model.getUser().setS_highscore(this.model.getTotalScore());
                    this.model.adjustHighScore();
                    newBest = true;
                }
                high = new Text("High score: " + this.model.getUser().getSeasonalHighscore());
            }
            else {
                if (this.model.getTotalScore() > this.model.getUser().getExpertHighscore()){
                    this.model.getUser().setE_highscore(this.model.getTotalScore());
                    this.model.adjustHighScore();
                    newBest = true;
                }
                high = new Text("High score: " + this.model.getUser().getExpertHighscore());
            }
            high.setFont(new Font(30));
        }
        VBox display = new VBox(10);
        if (newBest) {
            Text nB = new Text("New High Score!");
            nB.setFont(new Font(30));
            display.getChildren().add(nB);
        }
        display.getChildren().add(current);
        if (high != null) {
            display.getChildren().add(high);
        }

        display.getChildren().add(this.playAgain);
        if (this.model.getUser() != null) {
            display.getChildren().add(this.menu);
        }
        display.getChildren().add(this.exit);

        display.setStyle("-fx-alignment: center;");
        display.setTranslateY(-50);

        this.getChildren().addAll(this.myCanvas, display);
    }

    // TODO: x3

    /** Screen for viewing player history */
    private void createHistoryWindow() {
        this.createUserInfoBackground();

        // title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(new Font("Courier Prime", 36));
        this.gc.fillText("History", 680, 95);

        //TODO - add conditional here to see if user has history to pull
        // ie. if (history empty) show "No History"
        // else { (do all this)
        int hboxSpacer = 200;
        Text date = new Text("Date");
        date.setFont(new Font(32));
        Text diff = new Text("Difficulty");
        diff.setFont(new Font(32));
        Text score = new Text("Score");
        score.setFont(new Font(32));
        HBox header = new HBox(hboxSpacer, date, diff, score);

        //TODO add data from db to each row
        // add date(timestamp), difficulty, then score to each entry HBox

        HBox[] entries = new HBox[5];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Replace with actual DB Username and password
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");
            String query = "SELECT userdate, userdifficulty, gamescore FROM sql3765767.userhistory WHERE username = ? ORDER BY userdate DESC LIMIT 5";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, this.model.getUser().getUsername());
            ResultSet rs = stmt.executeQuery();

            int i = 0;
            while (rs.next() && i < 5) {
                Timestamp userdate = rs.getTimestamp("userdate");
                Date nonMilitaryDate = new Date(userdate.getTime());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                String finalDate = formatter.format(nonMilitaryDate);

                String userdifficulty = rs.getString("userdifficulty");
                int userscore = rs.getInt("gamescore");
                String scoreString = Integer.toString(userscore);

                Text timeLabel = new Text(finalDate);
                timeLabel.setStyle("-fx-font-size: 15px; -fx-fill: white; -fx-font-weight: bold;");
                Text difficultyLabel = new Text(userdifficulty);
                difficultyLabel.setStyle("-fx-font-size: 17px; -fx-fill: white; -fx-font-weight: bold;");
                Text scoreLabel = new Text(scoreString);
                scoreLabel.setStyle("-fx-font-size: 17px; -fx-fill: white; -fx-font-weight: bold;");


                entries[i] = new HBox(200);
                entries[i].getChildren().addAll(timeLabel, difficultyLabel, scoreLabel);
                i++;
            }
        } catch (Exception e){
                System.out.println(e.toString());
        }

        VBox entryContainer = new VBox(25, header);
        for (HBox entry: entries) {
            if (entry != null){
                entryContainer.getChildren().add(entry);
            }
        }
        entryContainer.setTranslateX(250);
        entryContainer.setTranslateY(175);

        this.getChildren().addAll(entryContainer);
    }


    /** Screen for viewing players pinned rounds */
    private void createPinnedWindow() {
        this.createUserInfoBackground();

        // Title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(new Font("Courier Prime", 36));
        this.gc.fillText("Pinned Locations", 680, 95);
    }

    /** Creates screen to show leaderboard */
    private void createLeaderboardWindow() {
        this.createUserInfoBackground();
        Pane layout = new Pane();

        // Title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(Font.font("Courier Prime",FontWeight.BOLD, 55));
        this.gc.fillText("Leaderboard", 720, 105);

        double borderX = 50;
        double borderY = 230;
        double borderWidth = 1100;
        double borderHeight = 470;
        this.gc.setFill(Color.rgb(10, 106, 66, 0.5));
        this.gc.fillRect(borderX, borderY, borderWidth, borderHeight);

        // My High Score button
        myHighScore.setTranslateX(50);
        myHighScore.setTranslateY(170);
        myHighScore.setOnMouseClicked(event -> {
            showDifficultyButtons(layout,true);
            myHighScore.setOpacity(0.5);
            top16.setOpacity(1.0);
        });

        // Top 16 button
        top16.setTranslateX(270);
        top16.setTranslateY(170);
        top16.setOnMouseClicked(event -> {
            showDifficultyButtons(layout,false);
            top16.setOpacity(0.5);
            myHighScore.setOpacity(1.0);
        });

        back2.setTranslateX(500);
        back2.setTranslateY(720);
        back2.setOnAction(event -> createLoggedInWindow());

        back3.setTranslateX(500);
        back3.setTranslateY(720);
        back3.setOnAction(event -> resetleaderboardView(layout));

        contentPane = new Pane();
        contentPane.getChildren().addAll(myHighScore, top16);

        layout.getChildren().addAll( contentPane,back2);
        this.getChildren().addAll(layout);
    }


    /**
     * Displays difficulty selection buttons (Novice, Seasonal, Expert) on the given layout.
     */
    private void showDifficultyButtons(Pane layout,boolean isMyhighScore){
        layout.getChildren().removeAll(Novice, Seasonal, Expert);

        showMyhighscore = isMyhighScore;
        showTop16 = !isMyhighScore;

        // 3 Difficulty buttons ( Novice, Seasonal, Expert )
        Novice.setTranslateX(500);
        Novice.setTranslateY(370);
        Novice.setOnAction(event -> {
            model.setCurrentDifficulty(DIFFICULTY.NOVICE);
            createGridDifficulty(layout, "Novice");
            createroundCombobox(layout, model, new myHighScoreGrid());
        });

        Seasonal.setTranslateX(500);
        Seasonal.setTranslateY(440);
        Seasonal.setOnAction(event -> {
            model.setCurrentDifficulty(DIFFICULTY.SEASONAL);
            createGridDifficulty(layout, "Seasonal");
            createroundCombobox(layout, model, new myHighScoreGrid());
        });

        Expert.setTranslateX(500);
        Expert.setTranslateY(510);
        Expert.setOnAction(event -> {
            model.setCurrentDifficulty(DIFFICULTY.EXPERT);
            createGridDifficulty(layout, "Expert");
            createroundCombobox(layout, model, new myHighScoreGrid());
        });

        if (!layout.getChildren().contains(Novice)){
            layout.getChildren().addAll( Novice, Seasonal, Expert);
        }

        if (!layout.getChildren().contains(back3)){
            layout.getChildren().add(back3);
        }
        back2.setVisible(false);
        back3.setVisible(true);
    }

    /**
     * Creates a ComboBox for selecting a round and adds it to the specified layout
     * When a round is selected, it updates the high score grid
     *
     * @param layout       The Pane to which the ComboBox will be added
     * @param model        The Model instance used for updating the high score grid
     * @param highScoreGrid The highScoreGrid instance responsible for rendering the high score data
     */
    private void createroundCombobox(Pane layout, Model model, myHighScoreGrid highScoreGrid){

        ObservableList<String> list = FXCollections.observableArrayList(
                "Round 1", "Round 2", "Round 3", "Round 4", "Round 5" );
        ComboBox<String> comboBox = new ComboBox<>(list);
        comboBox.setPromptText("Select a Round");
        comboBox.setTranslateX(950);
        comboBox.setTranslateY(250);

        comboBox.setOnAction(event -> {
            String selectedRound = comboBox.getValue();
            if (selectedRound != null) {
                int numberRound = Integer.parseInt((selectedRound.replaceAll("[^0-9]", "")));
                System.out.println("selected Round: " + numberRound);
                highScoreGrid.render(layout, model, numberRound);
            }
        });
        layout.getChildren().add(comboBox);
    }

    /**
     * Creates the grid based on your selected difficulty ( Easy, Medium, Hard )
     */
    private void createGridDifficulty(Pane layout, String difficulty){
        layout.getChildren().removeIf(node->(node instanceof Rectangle));

        // Create the grid
        RectangleGrid rectangleGrid = new RectangleGrid();

        rectangleGrid.createGrid(layout, showMyhighscore);

        if(!layout.getChildren().contains(myHighScore)){
            layout.getChildren().addAll(myHighScore, top16, back3);
        }
    }



    /**
     * Resets the leaderboards
     */
    private void resetleaderboardView(Pane layout){
        layout.getChildren().removeIf(node -> (node instanceof Rectangle));
        layout.getChildren().removeAll(Novice, Seasonal, Expert);

        if (!layout.getChildren().contains(myHighScore)){
            layout.getChildren().addAll(myHighScore, top16);
        }
        back2.setVisible(true);
        back3.setVisible(false);
        if (!layout.getChildren().contains(back2)){
            layout.getChildren().add(back2);
        }
    }


    /**
     * Helper function to create a grid which is used to create a leaderboard
     */
    private class RectangleGrid {
        private int selectedRound = 1;
        public void createGrid(Pane layout, boolean isMyHighScore) {
            layout.getChildren().clear();
            if (isMyHighScore) {
                new myHighScoreGrid().render(layout,model,selectedRound);
            } else {
                new top16Grid().render(layout, model);
            }
        }
    }


    /**
     * Helper function to set selected round
     */
    private void setSelectedRound(int numberRound){
        this.selectedRound = numberRound;
    }


    /**
     * Displays high scores for the user which is currently signed in
     */
    private class myHighScoreGrid {

        public void render(Pane layout, Model model, int numberRound) {
            layout.getChildren().clear();


            LinearGradient gradient2 = new LinearGradient(
                    0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(1, Color.rgb(128, 158, 128)),
                    new Stop(0, Color.rgb(10, 106, 66)),      // Start color (Usask green
                    new Stop(3, Color.rgb(128, 158, 128))     // End color (lighter green)
            );
            LinearGradient gradient = new LinearGradient(
                    0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(1, Color.rgb(41, 153, 41)),      // End color (lighter green)
                    new Stop(0, Color.rgb(20, 150, 100)),     // Start color (Usask green
                    new Stop(1, Color.rgb(41, 153, 41))       // End color (lighter green)
            );

            if (model.getUser() == null) {
                System.out.println("Error: User not logged in, Cannot retrieve");
                return;
            }

            //Scores Exist?
            List<Model.ScoreEntry> scores = model.getHighScoresRound(numberRound);
            if (scores == null || scores.isEmpty()) {
                Text noScores = new Text(400, 400, "No scores available");
                noScores.setFill(Color.WHITE);
                noScores.setFont(Font.font("Courier Prime", FontWeight.BOLD, 16));
                layout.getChildren().add(noScores);
                return;
            }

            int i =0;
            for (Model.ScoreEntry entry : scores) {
                if(!entry.username.equals((model.getUser().getUsername()))) continue;

                LinearGradient color = (i % 2 == 0) ? gradient : gradient2;

                Rectangle rectangle = new Rectangle(350, 350 + i * 40, 500, 35);
                rectangle.setFill(color);
                layout.getChildren().add(rectangle);

                // Ranking
                Text rankText = new Text(370, 350 + i * 40 + 25, String.valueOf(i + 1));
                rankText.setFill(Color.WHITE);
                rankText.setFont(Font.font("Courier Prime", FontWeight.BOLD, 16));
                layout.getChildren().add(rankText);

                // Highscore
                Text scoreText = new Text(420, 350 + i * 40 + 25, entry.username + " - " + entry.score);
                scoreText.setFill(Color.WHITE);
                scoreText.setFont(Font.font("Courier Prime", FontWeight.BOLD, 16));
                layout.getChildren().add(scoreText);

            }
        }
    }


    /**
     * Displays a grid which contains the top 16 high scores
     */
    private class top16Grid {
        public void render (Pane layout, Model model) {
            layout.getChildren().clear();
            LinearGradient gradient2 = new LinearGradient(
                    0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(10, 106, 66)),
                    new Stop(0.5, Color.rgb(20, 150, 100))
            );
            LinearGradient gradient = new LinearGradient(
                    0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(10, 106, 66)),
                    new Stop(0.5, Color.rgb(80, 180, 140))
            );
            List<Model.ScoreEntry> scores = model.getTop16scores();
            if (scores == null || scores.isEmpty()) {
                Text noScores = new Text(400, 400, "No scores available");
                noScores.setFill(Color.WHITE);
                noScores.setFont(Font.font("Courier Prime", FontWeight.BOLD, 16));
                layout.getChildren().add(noScores);
                return;
            }
            for (int i = 0; i < scores.size(); i++) {
                LinearGradient color = (i % 2 == 0) ? gradient : gradient2;

                double x = (i <8) ? 70: 630;
                double y = 350 + (i % 8) *40;

                Rectangle rectangle = new Rectangle(x,y, 500, 35);
                rectangle.setFill(color);
                layout.getChildren().add(rectangle);

                Text rankText = new Text(x + 10, y+ 25, String.valueOf(i + 1));
                rankText.setFill(Color.WHITE);
                rankText.setFont(Font.font("Courier Prime", FontWeight.BOLD, 16));
                layout.getChildren().add(rankText);

                Model.ScoreEntry entry = scores.get(i);
                Text userText = new Text(x +60, y + 25, entry.username + " - " + entry.score);
                userText.setFill(Color.WHITE);
                userText.setFont(Font.font("Courier Prime", FontWeight.BOLD, 16));
                layout.getChildren().add(userText);

            }

        }
    }


    /**
     * Creates the background for displaying user information.
     */
    private void createUserInfoBackground() {
        this.resetView();

        // Usask Green
        this.gc.setFill(Color.rgb(20, 150, 100));
        //this.gc.setFill(Color.DIMGREY);
        this.gc.fillRect(0, 145, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        this.gc.setFill(Color.BLACK);
        this.gc.setLineWidth(5);
        this.gc.strokeLine(0, 145, this.myCanvas.getWidth(), 145);

        // Usask Logo with University Saskatchewan
        Image l = new Image(Objects.requireNonNull(getClass().getResource("/USaskOffical/usask_usask_colour.png")).toExternalForm());
        ImageView logo = new ImageView(l);
        logo.setFitHeight(250);
        logo.setFitWidth(250);
        logo.setPreserveRatio(true);
        logo.setTranslateX(-425);
        logo.setTranslateY(-325);

        // Gradient color for box
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(0.5, Color.rgb(20, 150, 100))
        );

        // Draw Labels for Buttons
        double textbX = 650;
        double textbY = 40;
        double textWidth = 600;
        double textHeight = 75;
        double skew = 50;
        this.gc.setFill(gradient);

        // Parallelogram shape
        double[] xP = {textbX, textbX + textWidth, textbX + textWidth - skew, textbX - skew};
        double[] yP = {textbY, textbY, textbY + textHeight, textbY + textHeight};
        this.gc.fillPolygon(xP, yP, 4);

        // Green border between the photo and the green username box
        double borderX = 0;
        double borderY = 115;
        double borderWidth = 1200;
        double borderHeight = 30;
        this.gc.setFill(Color.rgb(20, 150, 100));
        this.gc.fillRect(borderX, borderY, borderWidth, borderHeight);

        // Back button in bottom-left corner
        VBox buttonContainer = new VBox(this.back2);
        buttonContainer.setTranslateX(10);
        buttonContainer.setTranslateY(720);

        this.getChildren().addAll(this.myCanvas, logo, buttonContainer);
    }



    //////// SETUP METHODS \\\\\\\\\
    /** Connect Model */
    public void setModel(Model m) {
        this.model = m;
        selectMainMenu();   // change this if you want to test a certain window
    }

    /**
     * Gets called when the model signals a change,
     * then based on what state the model is in, show/update
     * the appropriate window
     */
    @Override
    public void modelUpdated() {
        // call needed methods based on what changed
        switch (this.model.getCurrentWindow()) {
            case STARTUP -> selectMainMenu();
            case DIFF -> selectDifficultyWindow();
            case GAMEPLAY -> selectGameplayWindow();
            case LOGIN -> loginWindow();
            case CREATE -> createAccWindow();
            case END -> createEndScreen();
            case LOGGED_IN -> createLoggedInWindow();
            case HISTORY -> createHistoryWindow();
            case LEADERBOARD -> createLeaderboardWindow();
            case PINNED -> createPinnedWindow();
        }
    }

}