package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

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

/** Class that manages all the data
 * Controller calls Model methods to act on things like pictures
 * This is where we pass in the coordinates of mouse locations */
public class Model {

    // Model attributes
    private ArrayList<Subscriber> subscribers;
    private ArrayList<Picture> pictures;
    private int picIndex;
    private DISPLAY currentWindow; // This field's value will tell the view what to do

    private User user;
    private int round;          // round number out of 5 (5/5 = last round)
    private double totalScore;     // total sum score over rounds
    private double recentScore;    // most recent score
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
    public boolean verifyLogin(String username, String password) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Replace with actual DB Username and password
            Connection con = DriverManager.getConnection("jdbc:mysql://sql3.freesqldatabase.com:3306", "sql3765767", "McsStSMGU6");
            System.out.println("Connected to database");

            //Retrieve user and password fields and checksxs
            String query = "SELECT password, high_score FROM sql3765767.users WHERE username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                String hashPassword = rs.getString("password");
                int high_score = rs.getInt("high_score");
                if (BCrypt.checkpw(password, hashPassword)){
                    System.out.println("Login successful!");
                    user = new User(username, high_score, 0);
                    showLoggedInWindow();
                    return true;
                }
            }
            showLoginWindow();
            System.out.println("Password not correct!");
            return false;
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }

    }

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
            user = new User(username, 0, 0);
            String query = "INSERT INTO sql3765767.users(username, password, high_score) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword);
            stmt.setInt(3, 0);
            stmt.executeUpdate();
            System.out.println("Added to Database!");
            showLoggedInWindow();
        } catch (Exception e) {
            System.out.println(e.toString());
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

    /** GETTERS */
    public DISPLAY getCurrentWindow() {
        return currentWindow;
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
    public double getTotalScore() {
        return this.totalScore;
    }
    public double getRecentScore() {
        return this.recentScore;
    }
    public User getUser() {
        return this.user;
    }

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
    public double calculateScore(double distance) {
        double score = (1000 - distance * 5);
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
