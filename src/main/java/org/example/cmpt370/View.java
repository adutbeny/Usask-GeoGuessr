package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import javafx.concurrent.Worker;
import javafx.scene.Scene;
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

import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import java.awt.Desktop;
import java.net.URI;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;




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
    // create account
    public TextField usernameCreate;
    public PasswordField passwordCreate;
    public Button submitCreate;
    // end screen
    public Button playAgain;
    public Button exit;

    // gameplay loop
    public Button submit;
    public Button next;

    // logged in
    public Button history;
    public Button pinned;
    public Button leaderboard;
    public Button multiplayer;
    public Button back2;        // returns to logged in window

    public Button googleSignIn; // Button for Google Sign-In


    // Need these to implement google sign in
    private WebView googleWebView;
    private WebEngine googleWebEngine;


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
        buttonStack.getChildren().add(this.exit);
        // set below text
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // add all to layout in order!!!
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
        logo.setTranslateY(20);

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
        this.gc.fillText("Score", textbX + 230, textbY + 25);
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


        mapView.setPrefSize(400, 400);
        mapView.relocate(775, 200);

        // loads map api with html file
        mapEngine = mapView.getEngine();

        //this is checking for errors its not loading correctly
        mapEngine.setOnError(event -> System.out.println("WebView Error: " + event.getMessage()));
        mapEngine.setOnAlert(event -> {
            String alertMessage = event.getData();
            System.out.println("WebView Alert:" + alertMessage);
        });
        mapEngine.setJavaScriptEnabled(true);

        // this is for connecting the html to our java so we can get the coords
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
            mapView.setPrefSize(400, 400);
            mapView.relocate(775, 200);
        });


        this.model.setJavaConnector(connector); //store in model

        //load the map from the html file
        mapEngine.load(Objects.requireNonNull(getClass().getResource("/public/map.html")).toExternalForm());

        //Map interactions
        mapView.setOnMousePressed(event -> {
            mapView.setUserData(new double[]{event.getSceneX(), event.getY(), mapView.getLayoutX(), mapView.getLayoutY()});
        });

        layout.getChildren().add(0, c);
        layout.getChildren().add(mapView);
        layout.getChildren().add(logo);
        this.getChildren().addAll(this.myCanvas, c, layout, buttonStack);
    }

    ;

    /**
     * this sends needed info to our map html so it can create a line between two points
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
        // call this to allow google sign ins
        startAuthServer();

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

        this.googleSignIn.setOnAction(event -> openGoogleSignInPage());

        // add to layout
        Pane layout = new Pane();
        layout.getChildren().addAll(this.usernameField, this.passwordField, this.submitLogin, this.googleSignIn, this.back1);
        this.getChildren().addAll(this.myCanvas, layout);
    }


    private void openGoogleSignInPage() {
        try {
            // load the HTML file as an InputStream from resources
            InputStream inputStream = getClass().getResourceAsStream("/public/googleSignIN.html");

            if (inputStream == null) {
                System.out.println("Error: googleSignIN.html not found!");
                return;
            }

            // copy to a temp file
            File tempFile = File.createTempFile("googleSignIN", ".html");
            tempFile.deleteOnExit();
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // check if i can use someones default browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(tempFile.toURI());
            } else {
                System.out.println("Desktop browsing is not supported on this platform.");

                // we can try just rip chrome from their browser
                String browserPath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
                ProcessBuilder pb = new ProcessBuilder(browserPath, tempFile.toURI().toString());
                pb.start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void startAuthServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
            server.createContext("/auth", new AuthHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Auth server running at http://localhost:5000/auth");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // read in and parse the JSON request body
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    requestBody.append(line);
                }
                bufferedReader.close();

                // token
                String json = requestBody.toString();
                String token = json.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");

                System.out.println("Received Google Token: " + token);
            }

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            String response = "Token received!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
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

        // add to layout
        Pane layout = new Pane();
        layout.getChildren().addAll(this.usernameCreate, this.passwordCreate, this.submitCreate, this.back1);
        this.getChildren().addAll(this.myCanvas, layout);
    }

    /**
     * View for when the user has successfully logged in
     */
    public void createLoggedInWindow() {
        createDefaultBackground();

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 106, 66)),
                new Stop(1, Color.rgb(20, 150, 100))
        );

        // black outline
        this.gc.setFill(Color.BLACK);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                this.gc.fillText("Usask GeoGuesser", 400 + i, 270 + j);
            }
        }

        // Main text
        this.gc.setFill(gradient);
        this.gc.fillText("Usask GeoGuesser", 400, 270);

        // Button Stack
        VBox buttonStack = new VBox(20, this.quickplay, this.history, this.pinned, this.leaderboard, this.multiplayer, this.exit);
        buttonStack.setTranslateX(300);
        buttonStack.setTranslateY(350);

        // Add all to layout
        this.getChildren().addAll(buttonStack);
    }

    /**
     * Background for use with the log in and create account windows
     */
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
        this.resetView();

        // TODO: make this not temporary
        // green background
        this.gc.setFill(Color.rgb(10, 106, 66));
        this.gc.fillRect(0, 0, this.myCanvas.getWidth(), this.myCanvas.getHeight());

        Text current = new Text("Your Score: " + this.model.getTotalScore());
        current.setFont(new Font(30));
        Text high = null;
        if (this.model.getUser() != null) {
            if (this.model.getCurrentDifficulty() == DIFFICULTY.NOVICE){
                if (this.model.getTotalScore() > this.model.getUser().getNoviceHighscore()){
                    this.model.getUser().setN_highscore((int)this.model.getTotalScore());
                    this.model.adjustHighScore();
                }
                high = new Text("High score: " + this.model.getUser().getNoviceHighscore());
            }
            else if (this.model.getCurrentDifficulty() == DIFFICULTY.SEASONAL){
                if (this.model.getTotalScore() > this.model.getUser().getSeasonalHighscore()){
                    this.model.getUser().setS_highscore((int)this.model.getTotalScore());
                    this.model.adjustHighScore();
                }
                high = new Text("High score: " + this.model.getUser().getSeasonalHighscore());
            }
            else {
                if (this.model.getTotalScore() > this.model.getUser().getExpertHighscore()){
                    this.model.getUser().setE_highscore((int)this.model.getTotalScore());
                    this.model.adjustHighScore();
                }
                high = new Text("High score: " + this.model.getUser().getExpertHighscore());
            }
            high.setFont(new Font(30));
        }
        VBox display = new VBox(10, current);
        if (high != null) {
            display.getChildren().add(high);
        }

        display.getChildren().addAll(this.playAgain, this.exit);
        display.setStyle("-fx-alignment: center;");
        display.setTranslateY(-50);

        this.getChildren().addAll(this.myCanvas, display);
    }

    // TODO: x3

    /**
     * Screen for viewing player history
     */
    private void createHistoryWindow() {

        // make sure this window includes the 'this.back2' button
    }

    /**
     * Screen for viewing players pinned rounds
     */
    private void createPinnedWindow() {

        // make sure this window includes the 'this.back2' button
    }

    /**
     * Creates screen to show leaderboard
     */
    private void createLeaderboardWindow() {

        // make sure this window includes the 'this.back2' button
    }

    // SETUP METHODS

    /**
     * Connect Model
     */
    public void setModel(Model m) {
        this.model = m;
        selectMainMenu();
    }

    /**
     * Attaches itself to controller so that we can receive
     * interactions from the user and pass to the appropriate
     *
     * @param controller method
     */
    public void setupEvents(Controller controller) {
        myCanvas.setOnMousePressed(controller::handlePressed);
        myCanvas.setOnMouseDragged(controller::handleDragged);
        myCanvas.setOnMouseReleased(controller::handleReleased);
        setOnKeyPressed(controller::handleKeyPressed);
        myCanvas.setOnMouseMoved(controller::handleMouseMoved);

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