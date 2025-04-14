package org.example.cmpt370;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** this will handle all requests for multiplayer cloud database
 * will be using REST API to do this**/
public class FirebaseHelper {
    private static String FIREBASE_DB_URL;
    private HttpClient client;

    public FirebaseHelper() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("src/main/resources/config.properties"));
            FIREBASE_DB_URL = props.getProperty("FIREBASE_DB_URL");
            client = HttpClient.newHttpClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** this is for writing data to the cloud server for multiplayer
     * we format strings and then send it to the server to be kept as a json in a specified node
     * takes two strings and returns nothing**/
    public void writeData(String node, String jsonData) {
        try {
            String url = FIREBASE_DB_URL + "/" + node + ".json";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonData))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Write Response: " + response.body()); //for debugging
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** reads data from a node on the multiplayer server and returns it **/
    public String readData(String node) {
        try {
            String url = FIREBASE_DB_URL + "/" + node + ".json";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Read Data: " + response.body()); //for debugging
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
