package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** enum to signal different states within the model
 * Each value corresponds to a response from the view */
enum DISPLAY {
    STARTUP,
    DIFF,
    GAMEPLAY,
    LOGIN,
    CREATE,
    LOGGED_IN,
    HISTORY,
    PINNED,
    LEADERBOARD,
    END,
    MATCHMAKING,
    MULTIPLAYER,

}

// Store the selected difficulty
enum DIFFICULTY {
    NOVICE,
    SEASONAL,
    EXPERT
}

/** Class that manages all the data
 * Controller calls Model methods to act on things like pictures
 * while the View pulls needed data from the Model for display */
public class Model {

    // Model attributes
    private ArrayList<Subscriber> subscribers;      // connects to view
    private ArrayList<Picture> pictures;            // array of photos by difficulty
    private int picIndex;                           // current index of above array
    private Picture currentPicture;

    private DISPLAY currentWindow; // This field's value will tell the view what to do
    private DIFFICULTY currentDifficulty; //The current difficulty selected
    // User Data
    private User user;
    private int round;          // round number out of 5 (5/5 = last round)
    private int totalScore;     // total sum score over rounds
    private int recentScore;    // most recent score

    private boolean internet;   // boolean if connected to internet
    // Connection class for map API
    private JavaConnector connector;

    // Google Sign-in fields
    public Process pythonServerProcess;
    public boolean isPythonServerStarted = false;
    public GoogleAuthHandler googleAuthHandler;
    private boolean isGoogleSignIn = false;

    private ChatWindow chat;
    private String currentMatchId;
    private Multiplayer multiplayer;
    private boolean multiplayerMode = false;
    private double[] opponentGuess;
    public ScheduledExecutorService chatScheduler;

    /** Constructor */
    public Model() {
        this.googleAuthHandler = new GoogleAuthHandler(this);
        this.subscribers = new ArrayList<>();
        this.pictures = new ArrayList<>();
        this.round = 1;
        this.picIndex = 0;
        this.currentWindow = DISPLAY.STARTUP;
        this.currentDifficulty = null;
        this.internet = isInternetConnected();
        this.user = null;
    }

    /** Add any displays to the list of objects updated on
     * every change to Model
     * @param sub object to be added */
    public void addSubscriber(Subscriber sub) {
        this.subscribers.add(sub);
    }
    /** Notifies View of changes, call at end of any function that
     * should change what the view is displaying */
    public void notifySubscribers() {
        for (Subscriber sub : this.subscribers) {
            sub.modelUpdated();
        }
    }


    /** Take info from login and load into class instance */
    public boolean verifyLogin(String username, String password, boolean rememberMe) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            // retrieve user and password
            String query = "SELECT password, N_high_score, S_high_score, E_high_score FROM sql3765767.users WHERE username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashPassword = rs.getString("password");
                int N_high_score = rs.getInt("N_high_score");
                int S_high_score = rs.getInt("S_high_score");
                int E_high_score = rs.getInt("E_high_score");
                if (BCrypt.checkpw(password, hashPassword)) {
                    System.out.println("Login successful!");

                    // save credentials if "Remember Me" is checked
                    if (rememberMe) {
                        saveCredentials(username, password);
                    } else {
                        clearCredentials();
                    }

                    user = new User(username, N_high_score, S_high_score, E_high_score, 0);
                    showLoggedInWindow();
                    return true;
                }
            }
            showLoginWindow();
            System.out.println("Password not correct!");
            return false;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    /** Creates a new entry in the database with the provided login */
    public void createAccount(String username, String password) {
        try {
            if (username.length() > 50 || password.length() > 50){
                System.out.println("User and password must be less than 50 characters");
                return;
            }
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");
            String query = "SELECT COUNT(*) FROM sql3765767.users WHERE username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0){
                System.out.println("User already exists!");
                return;
            }
            user = new User(username, 0, 0, 0, 0);
            String querycreate = "INSERT INTO sql3765767.users(username, password, N_high_score, S_high_score, E_high_score) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement createstmt = con.prepareStatement(querycreate);
            createstmt.setString(1, username);
            createstmt.setString(2, hashPassword);
            createstmt.setInt(3, 0);
            createstmt.setInt(4, 0);
            createstmt.setInt(5, 0);
            createstmt.executeUpdate();
            System.out.println("Added to Database!");
            showLoggedInWindow();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /** Checks if a user already exists in the database
     * @param username The username to check.
     * @return True if the user exists, false otherwise. */
    private boolean userExists(String username) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            String query = "SELECT COUNT(*) FROM sql3765767.users WHERE username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // user exists !!!!!!!!!!!!
            }

            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error checking if user exists: " + e.getMessage());
        }
        return false; // user does not exist 1!!!!!!!!!
    }

    /** Sets whether the user is signing in with Google.
     * @param isGoogleSignIn True if the user is signing in with Google, false otherwise */
    public void setGoogleSignIn(boolean isGoogleSignIn) {
        this.isGoogleSignIn = isGoogleSignIn;
    }

    /** Initiates google sign in by triggering browser */
    public void initiateGoogleSignIn() {
        this.setGoogleSignIn(true);
        this.googleAuthHandler.startPythonServer();
        this.googleAuthHandler.openGoogleSignInPage();

        this.googleAuthHandler.startTokenChecker(() -> {
            System.out.println("Token received, updating UI...");
            this.showLoggedInWindow();
        });
    }

    public void createAccountFromEmail(String email) {
        try {
            String username = email.split("@")[0];

            if (userExists(username)) {
                System.out.println("User already exists. Logging in...");

                // DIRECTLY LOGIN GOOGLE USER WITH NO PASSWORD
                Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306",
                        "sql3765767", "McsStSMGU6");

                // LOAD USER DATA
                String query = "SELECT N_high_score, S_high_score, E_high_score FROM sql3765767.users WHERE username = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    user = new User(
                            username,
                            rs.getInt("N_high_score"),
                            rs.getInt("S_high_score"),
                            rs.getInt("E_high_score"),
                            0
                    );
                    System.out.println("Should log in.....");
                    showLoggedInWindow();
                }
                return;
            }

            // Create new Google user account (no password needed)
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306",
                    "sql3765767", "McsStSMGU6");

            String query = "INSERT INTO sql3765767.users(username, N_high_score, S_high_score, E_high_score) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setInt(2, 0);
            stmt.setInt(3, 0);
            stmt.setInt(4, 0);
            stmt.executeUpdate();

            user = new User(username, 0, 0, 0, 0);
            showLoggedInWindow();

        } catch (Exception e) {
            System.out.println("Error in Google login: " + e.getMessage());
        }
    }

    /** Saves the username and password to a file.
     * @param username The username to save.
     * @param password The password to save. */
    public void saveCredentials(String username, String password) {
        try (FileWriter writer = new FileWriter("credentials.txt")) {
            writer.write(username + "\n");
            writer.write(password);
        } catch (IOException e) {
            System.out.println("Error saving credentials: " + e.getMessage());
        }
    }

    /** Loads the saved username and password from a file.
     * @return An array containing the username and password, or null if the file doesn't exist */
    public String[] loadCredentials() {
        if (isGoogleSignIn) {
            return null; // skip loading if the user is using google
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("credentials.txt"))) {
            String username = reader.readLine();
            String password = reader.readLine();
            return new String[]{username, password};
        } catch (IOException e) {
            return null;
        }
    }

    /** Clears the saved credentials by deleting the file. */
    public void clearCredentials() {
        File file = new File("credentials.txt");
        if (file.exists()) {
            file.delete();
        }
    }


    public void setDifficulty(String csv){
        if (csv == null){
            System.out.println("Error: CSV is null.Defaulting to Novice");
            this.currentDifficulty = DIFFICULTY.NOVICE;
            return;
        }
        if (csv.equals("/BeginnerPhotos.csv")){
            this.currentDifficulty = DIFFICULTY.NOVICE;
        }
        else if (csv.equals("/MediumPictures.csv")){
            this.currentDifficulty = DIFFICULTY.SEASONAL;
        }
        else if (csv.equals("/HardPictures.csv")) {
            this.currentDifficulty = DIFFICULTY.EXPERT;
        }
    }

    public void setCurrentDifficulty(DIFFICULTY difficulty){
        if (difficulty == null){
            this.currentDifficulty = DIFFICULTY.NOVICE;
        } else {
            this.currentDifficulty = difficulty;
            System.out.println("Current Difficulty: " + this.currentDifficulty);
        }
    }

    /** Populates pictures array with the passed csv to it
     * @param csv filepath to appropriate csv */
    public void selectPictureSet(String csv) {
        this.pictures.clear(); // clear any existing data before loading new data
        this.picIndex = 0;

        // FileReader needs to catch IO Errors so we'll use try/catch
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(csv))))) {
            String line;
            // Read csv file line-by-line and populate picture array
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                String path = parts[0].trim();
                double latitude = Double.parseDouble(parts[1].trim());
                double longitude = Double.parseDouble(parts[2].trim());

                pictures.add(new Picture(path, latitude, longitude));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading pictures: " + e.getMessage());
        }
        if (!multiplayerMode) {
            Collections.shuffle(this.pictures); // put in random order
        }
        // This gets called on every consecutive play so we will reset model stats
        this.recentScore = 0;
        this.totalScore = 0;
        this.round = 1;
        this.getNextPic();
        this.showGameplayWindow();
    }

    /** Modifies data entry in database according to which difficulty level
     * the user set a new high score in
     * @return true or false based on success */
    public boolean adjustHighScore(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");
            if (currentDifficulty == DIFFICULTY.NOVICE) {
                String query = "UPDATE sql3765767.users SET N_high_score = ? WHERE username = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, user.getNoviceHighscore());
                stmt.setString(2, user.getUsername());
                stmt.executeUpdate();
                System.out.println("Adjusted a rookie high score!");
            }
            else if (currentDifficulty == DIFFICULTY.SEASONAL){
                String query = "UPDATE sql3765767.users SET S_high_score = ? WHERE username = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                System.out.println("This is my username" + user.getUsername());
                System.out.println("This is my high score" + user.getSeasonalHighscore());
                stmt.setInt(1, user.getSeasonalHighscore());
                stmt.setString(2, user.getUsername());
                stmt.executeUpdate();
                System.out.println("Adjusted a intermediate high score! of " + user.getSeasonalHighscore());
            }
            else{
                String query = "UPDATE sql3765767.users SET E_high_score = ? WHERE username = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, user.getExpertHighscore());
                stmt.setString(2, user.getUsername());
                stmt.executeUpdate();
                System.out.println("Adjusted a expert high score!");
            }
        } catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    public class ScoreEntry{
        public String username;
        public int score;

        public ScoreEntry(String username, int score){
            this.username = username;
            this.score = score;
        }
    }

    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");

    }

    /** Retrieves current user scores for a specific round from user and their scores in Novice, Seasoned ad Expert and sorts them in descending order
     * @return int scores*/
    public List<Model.ScoreEntry> getHighScoresRound(int numberRound){
        List<Model.ScoreEntry> scores = new ArrayList<>();
        if(user ==null){
            System.out.println("Error: User is null, Cannot Retireve high scores");
            return scores;
        }
        if(currentDifficulty ==null){
            System.out.println("Error: currentDificulty is Null. Defaulting to NOVICE");
            currentDifficulty = DIFFICULTY.NOVICE;
        }

        String query;
        if (currentDifficulty == DIFFICULTY.NOVICE){
            query = "SELECT username, score FROM sql3765767.userhscorehistory WHERE username = ? AND difficulty = 'Novice' ORDER BY score DESC LIMIT 8";
        }

        else if (currentDifficulty == DIFFICULTY.SEASONAL){
            query = "SELECT username, score FROM sql3765767.userhscorehistory WHERE username = ? AND difficulty = 'Seasonal' ORDER BY score DESC LIMIT 8";
        }
        else{
            query = "SELECT username, score FROM sql3765767.userhscorehistory WHERE username = ? AND difficulty = 'Expert' ORDER BY score DESC LIMIT 8";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connected to database");

            try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, user.getUsername());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        scores.add(new Model.ScoreEntry(rs.getString("username"), rs.getInt("score")));
                    }
                }
            }
            System.out.println("Successful: Retrieved my high scores for the difficulty");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return scores;
    }


    /** Retrieves all user scores from user dataset for Novice, Seasoned and Expert and sorts them in descending order
     * @return int scores*/
    public List<Model.ScoreEntry> getTop16scores(){
        List<Model.ScoreEntry> scores = new ArrayList<>();
        if(user ==null){
            System.out.println("Error: User is null, Cannot Retireve high scores");
            return scores;
        }
        if(currentDifficulty ==null){
            System.out.println("Error: currentDificulty is Null. Defaulting to NOVICE");
            currentDifficulty = DIFFICULTY.NOVICE;
        }
        String column;
        switch (currentDifficulty) {
            case NOVICE:
                column = "N_high_score";
                break;
            case SEASONAL:
                column = "S_high_score";
                break;
            default:
                column = "E_high_score";
                break;
        }

        String query = "SELECT username, " + column + " FROM sql3765767.users ORDER BY " + column +" DESC LIMIT 16";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connected to database");

            try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query)) {
                try(ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        scores.add(new Model.ScoreEntry(rs.getString("username"), rs.getInt(column)));
                    }
                }
            }
            System.out.println("Successful: Retrieved my high scores for the difficulty");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void CheckForPersonalBest(int score){
        String difficulty = "";
        try {
            if (currentDifficulty == DIFFICULTY.NOVICE){
                difficulty = "Novice";
            }
            else if (currentDifficulty == DIFFICULTY.SEASONAL){
                difficulty = "Seasonal";
            }
            else{
                difficulty = "Expert";
            }
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            String deleteOldest = "DELETE FROM sql3765767.userhscorehistory WHERE username = ? AND difficulty = ? ORDER BY score ASC LIMIT 1";
            String countquery = "SELECT COUNT(*) FROM sql3765767.userhscorehistory WHERE username = ? AND difficulty = ?";
            String findLowest = "SELECT MIN(score) FROM sql3765767.userhscorehistory WHERE username = ? AND difficulty = ?";

            PreparedStatement loweststmt = con.prepareStatement(findLowest);
            loweststmt.setString(1, user.getUsername());
            loweststmt.setString(2, difficulty);
            ResultSet lowestScore = loweststmt.executeQuery();

            int lowScore = 0;
            if (lowestScore.next()){
                lowScore = lowestScore.getInt(1);
            }

            PreparedStatement countstmt = con.prepareStatement(countquery);
            countstmt.setString(1, user.getUsername());
            countstmt.setString(2, difficulty);
            ResultSet countset = countstmt.executeQuery();

            boolean difficultyAmount = false;
            if (countset.next() && countset.getInt(1) >= 8){
                difficultyAmount = true;
            }

            if (lowScore < score && difficultyAmount){
                PreparedStatement deletestmt = con.prepareStatement(deleteOldest);
                deletestmt.setString(1, user.getUsername());
                deletestmt.setString(2, difficulty);
                deletestmt.executeUpdate();
            }

            String querycreate = "INSERT INTO sql3765767.userhscorehistory(username, difficulty, score) " + "VALUES (?, ?, ?)";
            PreparedStatement createstmt = con.prepareStatement(querycreate);
            createstmt.setString(1, user.getUsername());
            createstmt.setString(2, difficulty);
            createstmt.setInt(3, score);
            createstmt.executeUpdate();
            System.out.println("Added to History!");
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public void saveHistory(int score){
        String difficulty = "";
        try {
            if (currentDifficulty == DIFFICULTY.NOVICE){
                difficulty = "Novice";
            }
            else if (currentDifficulty == DIFFICULTY.SEASONAL){
                difficulty = "Seasonal";
            }
            else{
                difficulty = "Expert";
            }
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            String deleteOldest = "DELETE FROM sql3765767.userhistory WHERE username = ? ORDER BY userdate ASC LIMIT 1";
            String countquery = "SELECT COUNT(*) FROM sql3765767.userhistory WHERE username = ?";

            PreparedStatement countstmt = con.prepareStatement(countquery);
            countstmt.setString(1, user.getUsername());
            ResultSet countset = countstmt.executeQuery();

            if (countset.next() && countset.getInt(1) >= 5){
                PreparedStatement deletestmt = con.prepareStatement(deleteOldest);
                deletestmt.setString(1, user.getUsername());
                deletestmt.executeUpdate();
            }

            String querycreate = "INSERT INTO sql3765767.userhistory(username, userdate, userdifficulty, gamescore) " + "VALUES (?, CONVERT_TZ(UTC_TIMESTAMP(), 'UTC', 'America/Regina'), ?, ?)";
            PreparedStatement createstmt = con.prepareStatement(querycreate);
            createstmt.setString(1, user.getUsername());
            createstmt.setString(2, difficulty);
            createstmt.setInt(3, score);
            createstmt.executeUpdate();
            System.out.println("Added to History!");
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }


    /** GETTERS */
    public DISPLAY getCurrentWindow() {
        return currentWindow;
    }
    public DIFFICULTY getCurrentDifficulty(){
        return currentDifficulty;
    }
    public int getRound() {
        return round;
    }
    public boolean getInternetStatus() {
        return this.internet;
    }
    public Picture getCurrentPicture() {
        return currentPicture;
    }
    public int getTotalScore() {
        return this.totalScore;
    }
    public int getRecentScore() {
        return this.recentScore;
    }
    public User getUser() {
        return this.user;
    }


    // PICTURE METHODS
    /** Gets the next picture from the shuffled array
     * Works such that we won't get duplicates in the same round
     * and doesn't skip the first one */
    public Picture getNextPic() {
        if (pictures.isEmpty()) {
            return null;
        }
        Picture current = this.pictures.get(this.picIndex);
        this.picIndex++;
        this.currentPicture = current;
        return current;
    }

    /** Adds current photo to the users pinned */
    public void pin() {
        Picture current = this.getCurrentPicture();
        String picturePath = current.getPath();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            // replace with actual DB username and password
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            String addQuery = "INSERT into sql3765767.userpinned(username, pinnedlocation, latitude, longitude) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(addQuery);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, picturePath);
            stmt.setDouble(3, current.getLatitude());
            stmt.setDouble(4, current.getLongitude());
            stmt.executeUpdate();
            System.out.println("Path added to pinned database!");

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        // TODO add current to the database
    }

    /** Unpin photo from the users pinned list
     * Won't be the current photo, need to get from the database,
     * maybe based off index of where the picture is in the list
     * of pinned pictures? */
    public void unpin(String username, String path) {
        //TODO figure this out
        // update view to not show what we just removed
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // replace with actual DB username and password
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");
            notifySubscribers();
            String delete = "DELETE FROM sql3765767.userpinned WHERE username = ? AND pinnedlocation = ?";
            PreparedStatement query = con.prepareStatement(delete);
            query.setString(1, username);
            query.setString(2, path);
            query.executeUpdate();
            System.out.println("Pins updated");
        } catch (Exception e){
            System.out.println(e.toString());
        }
        notifySubscribers();
    }

    ///* METHODS TO CHANGE VIEW *\\\
    /** Prompts View to show start-up display */
    public void showStartupWindow() {
        this.currentWindow = DISPLAY.STARTUP;
        notifySubscribers();
    }
    /** Prompts View to show select difficulty display */
    public void showDifficultyWindow() {
        this.currentWindow = DISPLAY.DIFF;
        notifySubscribers();
    }
    /** Prompts View to show main gameplay window */
    public void showGameplayWindow() {
        this.currentWindow = DISPLAY.GAMEPLAY;
        notifySubscribers();
    }
    /** Prompts View to show Login window */
    public void showLoginWindow() {
        this.currentWindow = DISPLAY.LOGIN;
        notifySubscribers();
    }
    /** Prompts View to show Create Acc window */
    public void showCreateAccWindow() {
        this.currentWindow = DISPLAY.CREATE;
        notifySubscribers();
    }
    /** Prompts View to show logged in window */
    public void showLoggedInWindow() {
        this.currentWindow = DISPLAY.LOGGED_IN;
        notifySubscribers();
    }
    /** ... History window */
    public void showHistoryWindow() {
        this.currentWindow = DISPLAY.HISTORY;
        notifySubscribers();
    }
    /** ... Pinned locations */
    public void showPinnedWindow() {
        this.currentWindow = DISPLAY.PINNED;
        notifySubscribers();
    }
    /** ... Leaderboard */
    public void showLeaderboard() {
        this.currentWindow = DISPLAY.LEADERBOARD;
        notifySubscribers();
    }
    /** Prompts endgame screen for offline mode */
    public void showEndWindow() {
        this.currentWindow = DISPLAY.END;
        notifySubscribers();
    }

    public void showMatchmakingWindow(){
        this.currentWindow = DISPLAY.MATCHMAKING;
        notifySubscribers();
    }
    public void showMultiplayerWindow() {
        this.currentWindow = DISPLAY.MULTIPLAYER;
        notifySubscribers();
    }
    ///* MAP METHODS *\\\
    /** Get distance from guessed point to true point */
    public double getDistance() {
        // make sure it exists
        if (this.currentPicture == null) {
            System.out.println("No picture loaded.");
            return -1;
        }
        // get the marker coordinates from model
        if (this.connector == null) {
            System.out.println("Marker coordinates not set.");
            return -1;
        }

        // get pictures longitude and latitude, print statements for debugging
        double pictureLat = currentPicture.getLatitude();
        System.out.println("picture latitude:" + pictureLat);
        double pictureLng = currentPicture.getLongitude();
        System.out.println("picture longitude:" + pictureLng);

        // Gets marker coordinates from connector
        double markerLat = connector.getMarkerLat();
        System.out.println("marker latitude:" + markerLat);
        double markerLng = connector.getMarkerLng();
        System.out.println("marker longitude:" + markerLng);

        // find distance between and print to console for now
        double distance = this.haversine(pictureLat, pictureLng, markerLat, markerLng);
        System.out.println("You got: " + distance + " meters away!");

        // update scores
        this.recentScore = calculateScore(distance);
        this.totalScore += this.recentScore;
        return distance;
    }

    public void loadNextRound() {
        this.round++;
        if (this.round > 5) {
            this.showEndWindow();
            this.round = 1;
            return;
        }
        notifySubscribers(); // refresh view
    }

    /** This is to calculate the distances in meters between two cordinates **/
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = R * c;
        return Math.round(result);
    }

    /** Calculate score per guess based off distance */
    public int calculateScore(double distance) {
        int score = (int) (1000 - distance * 5);
        if (score < 0 || distance > 200.0) {
            return 0;
        } else {
            return score;
        }
    }

    /** Setter for java connector **/
    public void setJavaConnector(JavaConnector connector) {
        this.connector = connector;
    }

    /** Getter for java connector **/
    public JavaConnector getJavaConnector() {
        return connector;
    }

    /** Tests to see if the user is connected to the internet **/
    private boolean isInternetConnected() {
        try {
            URL test = new URL("https://git.cs.usask.ca");
            URLConnection connection = test.openConnection();
            connection.connect();
            return true;

        } catch (RuntimeException e) {
            // failed to connect due to network
            return false;
        } catch (IOException e) {
            // failed due to error in URL
            System.out.println("Error in getting URL connection test");
            return false;
        }
    }

    // MULTIPLAYER
    /** constantly polls the db to check if there are more than two people
     * waiting in the queue, if yes, attempt to host the match, the player who
     * joined the wait list first should have a lower time stamp and will be the
     * player suggested to host the game, this forces one single instance of "match"
     * in the db, sets currentMatchId so we can use it in the future for guesses and score**/
    public void startMatchmaking() {
        // init multiplayer instance and join wait queue
        setMultiplayerMode(true);
        this.multiplayer = new Multiplayer(user.getUsername());
        this.multiplayer.joinWaitQueue();

        // this uses a scheduler to run every second so that we are constantly polling
        // matchPlayers() and findMatchForOpponent(), these ensure only one match is created for two players
        while(true) {
            // attempts to create a match by hosting
            String matchId = this.multiplayer.matchPlayers();
            if (matchId != null) {
                setCurrentMatchId(matchId);
                System.out.println("match id is: " + matchId);
                break;
            }
            // if not the host (you have lower timestamp than others)
            // or if no other player in wait queue
            matchId = multiplayer.findMatchForOpponent();
            if (matchId != null) {
                setCurrentMatchId(matchId);
                System.out.println("match found for opponent: " + matchId);
                break;
            } else {
                System.out.println("still waiting for a match...");
            }
        }

        this.chat = new ChatWindow(this);
        this.chat.addMessage("chat Connected successfully", false);

        // start poll for messages to receive
        this.pollIncomingMessage();

        this.selectPictureSet("/BeginnerPhotos.csv");
        this.setDifficulty("/BeginnerPhotos.csv");
    }

    /**  multiplayer version of calculate score will need to adjust a lot **/
    public double calculateMultiplayerScore() {
        if (this.currentPicture == null) {
            System.out.println("no picture loaded");
            return -1;
        }
        if (this.connector == null) {
            System.out.println("no maker coords yet");
            return -1;
        }
        double pictureLat = currentPicture.getLatitude();
        double pictureLng = currentPicture.getLongitude();
        double markerLat = connector.getMarkerLat();
        double markerLng = connector.getMarkerLng();
        if (currentMatchId == null) {
            System.out.println("no match set");
            return -1;
        }
        // record player guess in firebase
        multiplayer.recordGuess(currentMatchId, markerLat, markerLng);

        // polling for opponents guess herte
        double[] oppGuess = null;
        int attempts = 0;
        while (oppGuess == null && attempts < 10) { // this should wait 30 seconds
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            oppGuess = multiplayer.readOpponentGuess(currentMatchId);
            if (oppGuess != null) {
                this.opponentGuess = oppGuess;
            } else {
                System.out.println("opponents guess not recieved");
            }
            attempts++;
        }
        double distance = haversine(pictureLat, pictureLng, markerLat, markerLng);
        System.out.println("you got: " + distance + " meters away!");
        this.recentScore = calculateScore(distance);
        this.totalScore += this.recentScore;
        return distance;
    }

    // getter for opponents guess, this allows us to get the distance of opponents guess from controller
    public double[] getOpponentGuess() {
        return opponentGuess;
    }
    // clears the guesses in firebase
    public void clearMultiplayerGuesses() {
        if (currentMatchId != null) {
            multiplayer.clearGuesses(currentMatchId);
        }
    }
    // set mutliplayer mode, takes boolean parameter
    public void setMultiplayerMode(boolean mode) {
        this.multiplayerMode = mode;
    }
    // returns boolean true if multiplayer mode and false otherwise
    public boolean isMultiplayerMode() {
        return multiplayerMode;
    }
    // setter for current match id
    public void setCurrentMatchId(String matchId) {
        this.currentMatchId = matchId;
    }
    // getter for currentMatchId
    public String getCurrentMatchId() {
        return currentMatchId;
    }

    /** send a message to the other player */
    public void sendMessage(String msg) {
        this.multiplayer.sendChatMessage(this.getCurrentMatchId(), msg);
        this.chat.addMessage(msg, true);
    }
    /** Polling function that checks every two seconds for
     * an incoming message to receive from the other player.
     * Should be called on successful match and run until match ends */
    private void pollIncomingMessage() {
        this.chatScheduler = Executors.newScheduledThreadPool(1);
        this.chatScheduler.scheduleAtFixedRate(() -> {
            if (!this.multiplayerMode) {    // if we've left multiplayer mode, kill scheduler
                this.chatScheduler.shutdown();
            }
            String msg = this.multiplayer.receiveChatMessage();
            if (msg != null) {
                this.chat.addMessage(msg, false);
            }
            // killed from controller when user hits 'exit'
        }, 0, 2, TimeUnit.SECONDS); // check every 2 seconds
    }

}
