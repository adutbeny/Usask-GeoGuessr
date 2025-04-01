package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.geometry.Pos;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.util.Duration;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import javax.swing.text.IconView;


/** Class that handles all display output
 * Needs to be updated by the Model each time
 * the user does something that requires the screen
 * to change (display new picture, map, etc.) */
public class View extends StackPane implements Subscriber {

    private Model model;
    private ChatWindow chat;
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
    public Button addChat;

    // logged in
    public Button history;
    public Button pinned;
    public Button leaderboard;
    public Button multiplayer;
    public Button back2;        // returns to logged in window

    // Need these to implement google sign in
    public GoogleAuthHandler googleAuthHandler;

    public CheckBox RememberMe;

    //LeaderBoard
    public Button myHighScorebutton;
    public Button top16button;
    public Button Novice;
    public Button Seasonal;
    public Button Expert;
    private Pane contentPane;
    private boolean showMyhighscore = false;
    private boolean showtop16button = false;
    private int selectedRound;
    private boolean difficultyButtonShown = false;


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
        Image pinImage = new Image(getClass().getResource("/icon/whitepin.png").toExternalForm());
        ImageView pinImageView = new ImageView(pinImage);
        pinImageView.setFitWidth(20);
        pinImageView.setFitHeight(20);
        this.addPin = new Button();
        addPin.setGraphic(pinImageView);
        addPin.setTranslateX(-540);
        addPin.setTranslateY(100);
        //

        Image chatImage = new Image(getClass().getResource("/icon/whitemessage.png").toExternalForm());
        ImageView chatImageView = new ImageView(chatImage);

        chatImageView.setFitWidth(20);
        chatImageView.setFitHeight(20);

        this.addChat = new Button();
        addChat.setGraphic(chatImageView);
        addChat.setPrefWidth(25);
        addChat.setTranslateX(-540);
        addChat.setTranslateY(25);


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

        this.myHighScorebutton = new Button("My High Score");
        this.myHighScorebutton.setPrefWidth(200);

        this.top16button = new Button("Top 16");
        this.top16button.setPrefWidth(200);
        this.Novice = new Button("Novice");
        this.Novice.setPrefWidth(200);
        this.Seasonal = new Button("Seasonal");
        this.Seasonal.setPrefWidth(200);
        this.Expert = new Button("Expert");
        this.Expert.setPrefWidth(200);

        // add future buttons here
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
        Image background = new Image(Objects.requireNonNull(getClass().getResource("/OtherAssets/betterfiller_enhanced.jpeg")).toExternalForm());
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
        this.gc.setFont(Font.font("Segoe UI This", FontWeight.BOLD, 46));
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
        this.gc.setFont(new Font("Segoe UI This", 20));
        this.gc.fillText("Username", textbX + 30, textbY + 25);
        if (this.model.getUser() != null) {
            if (this.model.getCurrentDifficulty() == DIFFICULTY.NOVICE) {
                if (this.model.getRecentScore() > this.model.getUser().getNoviceHighscore()) {
                    this.gc.fillText("Score (New Best!)", textbX + 230, textbY + 25);
                } else {
                    this.gc.fillText("Score", textbX + 230, textbY + 25);
                }
            } else if (this.model.getCurrentDifficulty() == DIFFICULTY.SEASONAL) {
                if (this.model.getRecentScore() > this.model.getUser().getSeasonalHighscore()) {
                    this.gc.fillText("Score (New Best!)", textbX + 230, textbY + 25);
                } else {
                    this.gc.fillText("Score", textbX + 230, textbY + 25);
                }
            } else if (this.model.getCurrentDifficulty() == DIFFICULTY.EXPERT) {
                if (this.model.getRecentScore() > this.model.getUser().getExpertHighscore()) {
                    this.gc.fillText("Score (New Best!)", textbX + 230, textbY + 25);
                } else {
                    this.gc.fillText("Score", textbX + 230, textbY + 25);
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
        this.gc.setFont(Font.font("Segoe UI This", FontWeight.BOLD, 25));
        if (this.model.getUser() == null) {
            this.gc.fillText(String.valueOf("User"), textbX + 20, 105);
        } else {
            this.gc.fillText(String.valueOf(this.model.getUser().getUsername()), textbX + 20, 105);//Accounts for 10 char
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

        Image chatImage = new Image(getClass().getResource("/icon/whitemessage.png").toExternalForm());
        ImageView chatImageView = new ImageView(chatImage);

        chatImageView.setFitWidth(20);
        chatImageView.setFitHeight(20);

        //might delete this button
        this.back2.setTranslateX(-460);
        this.back2.setTranslateY(350);

        layout.getChildren().add(0, c);
        layout.getChildren().add(this.mapView);
        layout.getChildren().add(logo);
        this.getChildren().addAll(this.myCanvas, c, layout, buttonStack, this.addChat, addPin, this.back2);
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

    public void multiplayerUpdateMapOverlay(double p1_markerLat, double p1_markerLng,
                                            double p2_markerLat, double p2_markerLng,
                                            double pictureLat, double pictureLng,
                                            double p1_dist, double p2_dist) {
        String script = String.format(
                "showMultiplayerResult(%f, %f, %f, %f, %f, %f, %f, %f);",
                p1_markerLat, p1_markerLng, p2_markerLat, p2_markerLng, pictureLat, pictureLng, p1_dist, p2_dist
        );
        mapEngine.executeScript(script);
    }

    public void showMatchmakingWindow() {
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

        Text searchingText = new Text("SEARCHING");
        searchingText.setFill(Color.BLACK);
        searchingText.setFont(new Font("Segoe UI This", 30));
        // Position the text below the title; adjust as needed.
        searchingText.setTranslateX(400);
        searchingText.setTranslateY(320);

        // Add the animated text to your scene (assuming your view extends a Pane or similar).
        this.getChildren().add(searchingText);
        searchingText.toFront();

        // timeline to make the animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> searchingText.setText("SEARCHING")),
                new KeyFrame(Duration.seconds(1), e -> searchingText.setText("SEARCHING.")),
                new KeyFrame(Duration.seconds(2), e -> searchingText.setText("SEARCHING..")),
                new KeyFrame(Duration.seconds(3), e -> searchingText.setText("SEARCHING...")),
                new KeyFrame(Duration.seconds(4), e -> searchingText.setText("SEARCHING..."))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Displays fields to enter user information
     * Needs to connect to database to verify credentials
     * and then once verified should create User instance in model
     */
    public void loginWindow() {
        this.createSignInBackground();

        // Title
        this.gc.setFill(Color.BLACK);
        this.gc.setEffect(null);
        this.gc.setFont(Font.font("Segoe UI Bold", 36));
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

        this.RememberMe.setLayoutX(545);
        this.RememberMe.setLayoutY(570);

        String[] savedCredentials = model.loadCredentials();
        if (savedCredentials != null) {
            this.usernameField.setText(savedCredentials[0]);
            this.passwordField.setText(savedCredentials[1]);
            this.RememberMe.setSelected(true);
        }

        // add to layout
        Pane layout = new Pane();
        layout.getChildren().addAll(this.usernameField, this.passwordField, this.submitLogin, this.googleSignIn, this.back1, this.RememberMe);
        this.getChildren().addAll(this.myCanvas, layout);

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
        this.gc.setFill(Color.BLACK);
        this.gc.setEffect(null);
        this.gc.setFont(Font.font("Segoe UI Bold", 36));
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
        VBox buttonStack = new VBox(20, this.quickplay, this.history, this.pinned, this.leaderboard, this.multiplayer, this.back1);
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

        // green background
        this.gc.setFill(Color.rgb(10, 106, 66));
        this.gc.fillRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        Text current = new Text("Your Score: " + this.model.getTotalScore());
        current.setFont(new Font("Segoe UI This", 30));

        boolean newBest = false;
        Text high = null;
        this.model.CheckForPersonalBest(this.model.getTotalScore());
        if (this.model.getUser() != null) {
            this.model.saveHistory(this.model.getTotalScore());
            if (this.model.getCurrentDifficulty() == DIFFICULTY.NOVICE) {
                if (this.model.getTotalScore() > this.model.getUser().getNoviceHighscore()) {
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
            high.setFont(new Font("Segoe UI This:",30));
        }

        VBox display = new VBox(10);
        if (newBest) {
            Text nB = new Text("New High Score!");
            nB.setFont(new Font("Segoe UI Bold", 30));
            display.getChildren().add(nB);
        }
        display.getChildren().add(current);
        if (high != null) {
            display.getChildren().add(high);
        }

        if (this.model.isMultiplayerMode()) {
            Text oppScore = new Text(this.model.getOpponentUserName() + " got " + this.model.getOpponentScore());
            oppScore.setFont(new Font("Segoe UI Bold", 30));
            display.getChildren().add(oppScore);
        }

        if (!this.model.isMultiplayerMode()) {
            display.getChildren().add(this.playAgain);
        }
        if (this.model.getUser() != null) {
            display.getChildren().add(this.menu);
        }
        display.getChildren().add(this.exit);

        display.setStyle("-fx-alignment: center;");
        display.setTranslateY(-50);

        this.getChildren().addAll(this.myCanvas, display);
    }

    /** Screen for viewing player history */
    private void createHistoryWindow() {
        this.createUserInfoBackground();

        // title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(new Font("Segoe UI Bold", 55));
        this.gc.fillText("History", 680, 105);

        //TODO - add conditional here to see if user has history to pull
        // ie. if (history empty) show "No History"
        // else { (do all this)
        int hboxSpacer = 200;
        Text date = new Text("Date");
        date.setFont(new Font("Segoe UI This", 32));
        Text diff = new Text("Difficulty");
        diff.setFont(new Font("Segoe UI This", 32));
        Text score = new Text("Score");
        score.setFont(new Font("Segoe UI This", 32));
        HBox header = new HBox(hboxSpacer, date, diff, score);

        HBox[] entries = new HBox[5];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

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
            if (i == 0){

                Text noHistory = new Text("No History Found");
                Text Blank = new Text("");
                Text Blank2 = new Text("");
                noHistory.setStyle("-fx-font-size: 30px; -fx-fill: white; -fx-font-weight: bold;");
                entries[i] = new HBox(200);
                entries[i].getChildren().addAll(Blank, noHistory, Blank2);
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

        // Back button in bottom-left corner
        VBox buttonContainer = new VBox(this.back2);
        buttonContainer.setTranslateX(500);
        buttonContainer.setTranslateY(720);

        this.getChildren().addAll(entryContainer, buttonContainer);
    }


    /** Screen for viewing players pinned rounds */
    private void createPinnedWindow() {
        this.createUserInfoBackground();

        // Title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(new Font("Segoe UI This", 55));
        this.gc.fillText("Pinned Locations", 680, 105);

        VBox entryBox = new VBox(50);
        entryBox.setAlignment(Pos.TOP_CENTER);
        entryBox.setPrefWidth(800);
        entryBox.setFillWidth(true);

        ArrayList<Picture> pinnedLocations = new ArrayList<Picture>();
        int value = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");
            String query = "SELECT pinnedlocation, latitude, longitude FROM sql3765767.userpinned WHERE username = ? ORDER BY id DESC";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, this.model.getUser().getUsername());
            ResultSet rs = stmt.executeQuery();
            int mapCount = 0;
            while (rs.next()) {
                value++;
                String picturepath = rs.getString("pinnedlocation");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                Image loadedImage = new Image(getClass().getResourceAsStream(picturepath));
                ImageView imageView = new ImageView(loadedImage);
                imageView.setFitHeight(400);
                imageView.setFitWidth(400);

                WebView mapView = new WebView();
                mapView.setPrefSize(400,300);
                WebEngine mapEngine = mapView.getEngine();

                mapEngine.load(Objects.requireNonNull(getClass().getResource("/public/pinMap.html")).toExternalForm());

                mapEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        // Execute JavaScript to place the marker on the map
                        String script = String.format("showCorrect(%f, %f);", latitude, longitude);
                        mapEngine.executeScript(script);
                    }
                });


                Label latLabel = new Label("Latitude: " + latitude);
                latLabel.setFont(new Font("Segoe UI This", 25));
                Label longLabel = new Label("Longitude: " + longitude);
                longLabel.setFont(new Font("Segoe UI This", 25));
                Button unpin = new Button("Unpin");
                unpin.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: 'Segoe UI';");
                unpin.setOnAction(e -> this.model.unpin(this.model.getUser().getUsername(), picturepath));

                imageView.setFitWidth(300);
                imageView.setFitHeight(300);
                mapView.setPrefWidth(300);
                mapView.setPrefHeight(300);

                HBox entryRow = new HBox(imageView, mapView, unpin);
                entryBox.getChildren().add(entryRow);
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }
        if (value == 0) {
            Text noPins = new Text("No Locations Pinned");
            noPins.setFont(new Font("Segoe UI This", 32));
            entryBox.getChildren().add(noPins);
        }

        ScrollPane entryContainer = new ScrollPane(entryBox);
        entryContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #0A6A42, #084A2E)");
        entryBox.setStyle("-fx-background-color: linear-gradient(to bottom, #0A6A42, #084A2E)");


        entryContainer.setFitToWidth(true);
        entryContainer.setFitToHeight(true);
        entryContainer.setPrefWidth(800);
        entryContainer.setPrefHeight(500);
        entryContainer.setMaxHeight(500);
        entryContainer.setLayoutY(150);

        // Back button in bottom-left corner
        VBox buttonContainer = new VBox(this.back2);
        buttonContainer.setTranslateX(500);
        buttonContainer.setTranslateY(720);

        this.getChildren().addAll(entryContainer, buttonContainer);
    }

    /** Creates screen to show leaderboard */
    private void createLeaderboardWindow() {
        this.createUserInfoBackground();
        Pane layout = new Pane();

        // Title
        this.gc.setFill(Color.WHITE);
        this.gc.setFont(Font.font("Segoe UI Bold", 55));
        this.gc.fillText("Leaderboard", 680, 105);

        double borderX = 45;
        double borderY = 230;
        double borderWidth = 1110;
        double borderHeight = 470;
        this.gc.setFill(Color.rgb(10, 106, 66, 0.5));
        this.gc.fillRect(borderX, borderY, borderWidth, borderHeight);

        // My High Score button
        myHighScorebutton.setTranslateX(50);
        myHighScorebutton.setTranslateY(170);
        myHighScorebutton.setOnMouseClicked(event -> {
            showDifficultyButtons(layout, true);
            myHighScorebutton.setOpacity(0.5);
            top16button.setOpacity(1.0);
        });

        // Top 16 button
        top16button.setTranslateX(270);
        top16button.setTranslateY(170);
        top16button.setOnMouseClicked(event -> {
            showDifficultyButtons(layout, false);
            top16button.setOpacity(0.5);
            myHighScorebutton.setOpacity(1.0);
        });

        //back button
        back2.setTranslateX(500);
        back2.setTranslateY(720);
        back2.setOnAction(event -> {
            if (difficultyButtonShown) {
                resetleaderboardView(layout);
            } else {
                createLoggedInWindow();
            }
        });
            myHighScorebutton.setOpacity(1.0);
            top16button.setOpacity(1.0);

            contentPane = new Pane();
            contentPane.getChildren().addAll(myHighScorebutton, top16button);

            layout.getChildren().addAll(contentPane, back2);
            this.getChildren().addAll(layout);

    }


    /**
     * Displays difficulty selection buttons (Novice, Seasonal, Expert) on the given layout.
     */
    private void showDifficultyButtons(Pane layout, boolean isMyhighScore){
        layout.getChildren().removeAll(Novice, Seasonal, Expert);

        showMyhighscore = isMyhighScore;
      //  showTop16 = !isMyhighScore;

        // 3 Difficulty buttons ( Novice, Seasonal, Expert )
        Novice.setTranslateX(200);
        Novice.setTranslateY(270);
        Novice.setOnAction(event -> {
            model.setCurrentDifficulty(DIFFICULTY.NOVICE);
            createGridDifficulty(layout, "Novice");
            Novice.setOpacity(0.5);
            Seasonal.setOpacity(1.0);
            Expert.setOpacity(1.0);
        });

        Seasonal.setTranslateX(500);
        Seasonal.setTranslateY(270);
        Seasonal.setOnAction(event -> {
            model.setCurrentDifficulty(DIFFICULTY.SEASONAL);
            createGridDifficulty(layout, "Seasonal");
            Seasonal.setOpacity(0.5);
            Expert.setOpacity(1.0);
            Novice.setOpacity(1.0);
        });

        Expert.setTranslateX(800);
        Expert.setTranslateY(270);
        Expert.setOnAction(event -> {
            model.setCurrentDifficulty(DIFFICULTY.EXPERT);
            createGridDifficulty(layout, "Expert");
            Expert.setOpacity(0.5);
            Novice.setOpacity(1.0);
            Seasonal.setOpacity(1.0);
        });

        //Sets opacity back to normal when exited leaderboard
        Novice.setOpacity(1.0);
        Seasonal.setOpacity(1.0);
        Expert.setOpacity(1.0);

        if (!layout.getChildren().contains(Novice)){
            layout.getChildren().addAll( Novice, Seasonal, Expert);
        }
        back2.setText("Back");

    }

    /**
     * Creates the grid based on your selected difficulty ( Easy, Medium, Hard )
     */
    private void createGridDifficulty(Pane layout, String difficulty){
        layout.getChildren().removeIf(node->(node instanceof Rectangle));

        // Create the grid
        RectangleGrid rectangleGrid = new RectangleGrid();

        rectangleGrid.createGrid(layout, showMyhighscore);

        if(!layout.getChildren().contains(myHighScorebutton)){
            layout.getChildren().addAll(myHighScorebutton, top16button);
        }
    }



    /**
     * Resets the leaderboards
     */
    private void resetleaderboardView(Pane layout){
        layout.getChildren().removeIf(node -> (node instanceof Rectangle));
        layout.getChildren().removeAll(Novice, Seasonal, Expert);

        if (!layout.getChildren().contains(myHighScorebutton)) {
            layout.getChildren().addAll(myHighScorebutton, top16button);
        }
    }

    /**
     * Renders each row one by one with ranking number, username and score and highlights current user's ranking in Top16
     */

    public void renderRow(Pane layout, double x, double y, String username, int score, int i, boolean isCurentUser) {

        //create rows
        Rectangle rectangle = new Rectangle(x, y, 500, 35);
        if (isCurentUser) {
            rectangle.setFill(Color.rgb(255, 215, 0,0.25));
        }else{
            rectangle.setFill(Color.rgb(10, 106, 66));
        }
        layout.getChildren().add(rectangle);

        double rankX = x + 13;
        double poxY = y + 25;
        int rank = i + 1;

        // Ranking
        Text rankText = new Text(rankX, poxY, String.valueOf(rank));
        rankText.setFill(Color.WHITE);
        rankText.setFont(Font.font("Segoe UI Bold", 16));
        layout.getChildren().add(rankText);

        //Username
        Text usernameText = new Text(x + 60, poxY, username);
        usernameText.setFill(Color.WHITE);
        usernameText.setFont(Font.font("Segoe UI Bold", 16));
        layout.getChildren().add(usernameText);

        //Score
        Text scoreText = new Text(x + 390, poxY, String.valueOf(score));
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Segoe UI Bold", 16));
        layout.getChildren().add(scoreText);
    }

    /**
     * renders empty row with no information
     */

    protected void renderEmptyRows(Pane layout, double x, double startY, int maxRows) {
        for (int i = 0; i < maxRows; i++) renderRow(layout, x, startY + (i * 40),"",0,i,false);

    }

    private void setSelectedRound(int numberRound){
        this.selectedRound = numberRound;
    }

    /**
     * Displays high scores for the user which is currently signed in
     */
    private class myHighScoreGrid {

        public void render(Pane layout, Model model, int selectedRound) {
            layout.getChildren().removeIf(node -> (node instanceof Rectangle) || node instanceof Text);

            if (model.getUser() == null) {
                System.out.println("Error: User not logged in, Cannot retrieve");
                renderEmptyRows(layout, 350, 350, 8);
                return;
            }

            //Scores Exist?
            List<Model.ScoreEntry> scores = model.getHighScoresRound(selectedRound);

            int maxRows = 8;

            if (scores == null || scores.isEmpty()) {
                renderEmptyRows(layout, 350, 350, maxRows);
                return;
            }
            for (int i = 0; i < maxRows; i++){

                if (i < scores.size() && scores.get(i).username.equals(model.getUser().getUsername())) {
                    renderRow(layout, 350, 350 + (i*40), scores.get(i).username,scores.get(i).score,i,false);

                } else {
                    renderRow(layout, 350, 350 + (i*40), "",0,i,false);
                }
            }

        }
    }


    /**
     * Displays a grid which contains the top 16 high scores and feths data for the exact ranking of current user
     */
    private class top16Grid {
        public void render (Pane layout, Model model) {
            layout.getChildren().removeIf(node -> (node instanceof Rectangle) || node instanceof Text);

            //Scores Exist?
            List<Model.ScoreEntry> scores = model.getTop16scores();

            int maxRows = 8; //max num of rows in each column
            int totalRows = 16; // total rows in both column

            if (scores == null || scores.isEmpty()) {
                renderEmptyRows(layout, 70, 350, maxRows);
                renderEmptyRows(layout, 630, 350, maxRows);
                return;
            }

            for (int i = 0; i < totalRows; i++){
                double x = (i<maxRows) ? 70 : 630;//column positioning
                double y = 350 + (i % maxRows) * 40; //spacing in between rows

                if (i < scores.size()) {
                    Model.ScoreEntry entry = scores.get(i);
                    boolean isCurrentUser = model.getUser() != null && entry.username.equals(model.getUser().getUsername());
                    renderRow(layout, x, y , entry.username,entry.score,i,isCurrentUser);

                } else {
                    renderRow(layout, x,y, "",0,i,false);
                }

            }
        }
    }

    /**
     * Displays either My score or top16
     */

    private class RectangleGrid {
        private int selectedRound = 1;
        public void createGrid(Pane layout, boolean isMyHighScore) {

            if (isMyHighScore) {
                new myHighScoreGrid().render(layout,model,selectedRound);
            } else {
                new top16Grid().render(layout, model);
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
        buttonContainer.setTranslateX(500);
        buttonContainer.setTranslateY(720);

        this.getChildren().addAll(this.myCanvas, logo, buttonContainer);
    }


    public void clearLoginFields() {
        usernameField.clear();
        passwordField.clear();
        System.out.println("Cleared username and password fields.");
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
            case MATCHMAKING -> showMatchmakingWindow();
        }
    }

}