package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

enum DISPLAY {
    STARTUP,
    DIFF,
    GAMEPLAY,
    LOGIN,
    CREATE,
    LOGGED_IN,      // window when you have succesfully logged in
    HISTORY,
    PINNED,
    LEADERBOARD,
    END,

}

enum DIFFICULTY {
    NOVICE,
    SEASONAL,
    EXPERT
}

/** Class that manages all the data
 * Controller calls Model methods to act on things like pictures
 * This is where we pass in the coordinates of mouse locations */
public class Model {

    // Model attributes
    private ArrayList<Subscriber> subscribers;
    private ArrayList<Picture> pictures;
    private int picIndex;
    private DISPLAY currentWindow; // This field's value will tell the view what to do
    private DIFFICULTY currentDifficulty; //The current difficulty selected

    private User user;
    private int round;          // round number out of 5 (5/5 = last round)
    private int totalScore;     // total sum score over rounds
    private int recentScore;    // most recent score
    private boolean internet;   // boolean if connected to internet
    private JavaConnector connector;
    private Picture currentPicture;
    // etc...

    /** Constructor */
    public Model() {
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
    public void notifySubscribers() {
        for (Subscriber sub : this.subscribers) {
            sub.modelUpdated();
        }
    }

    /** Take info from login and load into class instance */
    public boolean verifyLogin(String username, String password, boolean rememberMe) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Replace with actual DB Username and password
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            // Retrieve user and password fields and checks
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

                    // Save credentials if "Remember Me" is checked
                    if (rememberMe) {
                        saveCredentials(username, password);
                    } else {
                        clearCredentials(); // Clear saved credentials if "Remember Me" is unchecked
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
            //Replace with actual DB Username and password
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

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    public void createAccountFromEmail(String email) {
        try {
            // Extract the username (part before @)
            String username = email.split("@")[0];

            // Generate a random password
            String password = generateRandomPassword(12); // 12-character password

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Insert into the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            String query = "INSERT INTO sql3765767.users(username, password, N_high_score, S_high_score, E_high_score) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setInt(3, 0); // Default novice high score
            stmt.setInt(4, 0); // Default seasonal high score
            stmt.setInt(5, 0); // Default expert high score
            stmt.executeUpdate();

            System.out.println("Account created for email: " + email);
            System.out.println("Username: " + username);
            System.out.println("Random Password: " + password); // Log the password for debugging (remove in production)

            // Automatically log in the user after account creation
            verifyLogin(username, password, true); // Set "Remember Me" to true

            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }


    /**
     * Saves the username and password to a file.
     *
     * @param username The username to save.
     * @param password The password to save.
     */
    public void saveCredentials(String username, String password) {
        try (FileWriter writer = new FileWriter("credentials.txt")) {
            writer.write(username + "\n");
            writer.write(password);
        } catch (IOException e) {
            System.out.println("Error saving credentials: " + e.getMessage());
        }
    }

    /**
     * Loads the saved username and password from a file.
     *
     * @return An array containing the username and password, or null if the file doesn't exist.
     */
    public String[] loadCredentials() {
        try (BufferedReader reader = new BufferedReader(new FileReader("credentials.txt"))) {
            String username = reader.readLine();
            String password = reader.readLine();
            return new String[]{username, password};
        } catch (IOException e) {
            return null; // File doesn't exist or couldn't be read
        }
    }

    /**
     * Clears the saved credentials by deleting the file.
     */
    public void clearCredentials() {
        File file = new File("credentials.txt");
        if (file.exists()) {
            file.delete();
        }
    }



    public void setDifficulty(String csv){
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

    /** Populates pictures array with the passed csv to it
     * @param csv filepath to appropriate csv */
    public void selectPictureSet(String csv) {
        this.pictures.clear(); // Clear any existing data before loading new data
        this.picIndex = 0;
        // FileReader needs to catch IO Errors so we'll use try/catch
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(csv))))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // Split by comma

                String path = parts[0].trim();
                double latitude = Double.parseDouble(parts[1].trim());
                double longitude = Double.parseDouble(parts[2].trim());

                pictures.add(new Picture(path, latitude, longitude)); // Add to list
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading pictures: " + e.getMessage());
        }

        Collections.shuffle(this.pictures); // put in random order
        this.getNextPic();
        this.showGameplayWindow();

    }

    /** Modifies data entry in database according to which difficulty level
     * the user set a new high score in
     * @return true or false based on success */
    public boolean adjustHighScore(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Replace with actual DB Username and password
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
            //Replace with actual DB Username and password
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
        // TODO add current to the database
    }
    /** Unpin photo from the users pinned list
     * Won't be the current photo, need to get from the database,
     * maybe based off index of where the picture is in the list
     * of pinned pictures? */
    public void unpin(/* might need an argument*/) {
        //TODO figure this out
        // update view to not show what we just removed
        notifySubscribers();
    }

    /* METHODS TO CHANGE VIEW */

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

    /* MAP METHODS */
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
            // TODO: trigger end of game
            this.showEndWindow();
        }
        // cycle photo
        /** Breaks program. negative aura */
        //Picture next = this.getNextPic();
        notifySubscribers(); // refresh view
    }

    /** this is to calculate the distances in meters between two cordinates **/
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

    /** setter for java connector **/
    public void setJavaConnector(JavaConnector connector) {
        this.connector = connector;
    }

    /** getter for java connector **/
    public JavaConnector getJavaConnector() {
        return connector;
    }

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
            // failed due to error in url
            System.out.println("Error in getting URL connection test");
            return false;
        }
    }
}
