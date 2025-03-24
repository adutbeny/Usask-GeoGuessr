package org.example.cmpt370;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** this will handle all requests for multiplayer cloud database
 * will be using REST API to do this**/
public class FirebaseHelper {
    private static final String FIREBASE_DB_URL = "https://usask-geoguesser-default-rtdb.firebaseio.com/";
    private HttpClient client;

    public FirebaseHelper() {
        client = HttpClient.newHttpClient();
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

}
