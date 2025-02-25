package org.example.cmpt370;

public class JavaConnector {
    // this class is for the map, the html needs a way to expose the coordinates chosen to our java code
    private double markerLat;
    private double markerLng;

    public void updateMarkerCoordinates(double lat, double lng) {
        this.markerLat = lat;
        this.markerLng = lng;
        // debugging
        System.out.println("JavaConnector succesfully updated: " + lat + ", " + lng);
    }

    public double getMarkerLat() {
        return markerLat;
    }

    public double getMarkerLng() {
        return markerLng;
    }
}
