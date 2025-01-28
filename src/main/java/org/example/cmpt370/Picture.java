package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

/** Class to represent a photo, including file path and coords
 * Built into an array while parsing over csv on startup/mode select */
public class Picture {
    /* TODO:
     * Likely going to need a class to store a photo and its location
     * Here we can also add any methods we might need to act on it */
    private String path;
    private double latitude;
    private double longitude;

    /** Constructor for Picture class */
    public Picture(String path, double latitude, double longitude) {
        this.path = path;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    /** Returns path as string */
    public String getPath() {
        return path;
    }
    /** Returns lat */
    public double getLatitude() {
        return latitude;
    }
    /** Returns long */
    public double getLongitude() {
        return longitude;
    }
}
