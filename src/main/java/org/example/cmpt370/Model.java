package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

enum DISPLAY {
    STARTUP,
    DIFF,
    GAMEPLAY,
    LEADERBOARD
    //etc.
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
    // user attributes
    private String username;
    private int score;
    private int highscore;
    private int round;
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

    /* TODO:
     * things we will probably need
     * calculateDistance()
     * nextImage()
     * showMap()
     * etc
     */

    /** Populates pictures array with the passed csv to it
     * @param csv filepath to appropriate csv
     */
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
        this.showGameplayWindow();
    }

    /** GETTERS */
    public DISPLAY getCurrentWindow() {
        return currentWindow;
    }
    public String getUsername() {
        return username;
    }
    public int getScore() {
        return score;
    }
    public int getHighscore() {
        return highscore;
    }
    public int getRound() {
        return round;
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
    /** getter for current picture **/
    public Picture getCurrentPicture() {
        return currentPicture;
    }

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

    /** this is to calculate the distances in meters between two cordinates**/
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
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
    /** setter for java connector **/
    public void setJavaConnector(JavaConnector connector) {
        this.connector = connector;
    }
    /** getter for java connector **/
    public JavaConnector getJavaConnector() {
        return connector;
    }


}
