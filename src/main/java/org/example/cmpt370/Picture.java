package org.example.cmpt370;

/* Property of swagtown
 * CMPT370 */

/** Class to represent a photo, including file path and coords
 * Built into an array while parsing over a given csv on startup/mode select
 * These will be stored by the model to be displayed by the View*/
public class Picture {

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
