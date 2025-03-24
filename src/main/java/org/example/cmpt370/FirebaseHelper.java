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

}
