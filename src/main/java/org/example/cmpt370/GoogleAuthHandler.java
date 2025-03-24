package org.example.cmpt370;

import javafx.animation.AnimationTimer;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.Base64;
import java.util.concurrent.Executors;


/**
 * Handles Google Authentication using a local Python HTTP server
 * This class starts/stops the Python server, opens the Google Sign-In page
 * monitors authentication tokens, and extracts user information
 */
public class GoogleAuthHandler {

    private Process pythonServerProcess;

    // Flag to check if python server is already running
    private boolean isPythonServerStarted = false;

    private Model model;

    public GoogleAuthHandler() {
        this.model = new Model();
    }

    /**
     * Starts the Python server that handles authentication requests
     */
    public void startPythonServer() {
        if (isPythonServerStarted) {
            System.out.println("Python server is already running!");
            return;
        }
        try {
            // path to the Python server
            String pythonScriptPath = "src/main/python/server.py";

            // starting the Python server
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            processBuilder.redirectErrorStream(true);
            pythonServerProcess = processBuilder.start();

            // Very useful debugging to help fix issues w/ server
            Executors.newSingleThreadExecutor().submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pythonServerProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Python Server] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("Python server started.");
            isPythonServerStarted = true; // set the flag to true
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the Python server if it is running
     */
    public void stopPythonServer() {
        if (pythonServerProcess != null && pythonServerProcess.isAlive()) {
            pythonServerProcess.destroy();
            System.out.println("Python server stopped.");
            isPythonServerStarted = false;
        }
    }

    /**
     * Opens the Google Sign-In page in the default web browser.
     * Ensure the port number matches the Python server configuration!!
     */
    public void openGoogleSignInPage() {
        try {
            // open the Google Sign-In page ( check to make sure port # matches everywhere !!!!!!!!!!!!!!!!)
            Desktop.getDesktop().browse(new URI("http://localhost:63347/googleSignIN.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a background process to monitor the authentication token file.
     * When a token is received, it extracts the user's email and stops the Python server.
     *
     * @param onTokenReceived A callback function to execute after token retrieval.
     */
    public void startTokenChecker(Runnable onTokenReceived) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                String token = readTokenFromFile();
                if (token != null) {
                    System.out.println("Google Token: " + token);

                    // get emial from teken
                    String email = extractEmailFromIdToken(token);
                    if (email != null) {
                        System.out.println("User Email: " + email);

                        model.createAccountFromEmail(email);

                        // delete the token file
                        File tokenFile = new File("token.txt");
                        if (tokenFile.exists()) {
                            tokenFile.delete();
                            System.out.println("Deleted token.txt after processing.");
                        }

                        stopPythonServer(); // stop the Python server
                        this.stop();
                        onTokenReceived.run();
                    } else {
                        System.out.println("Failed to extract email from ID token.");
                    }
                }
            }
        };
        timer.start();
    }

    /**
     * Reads the authentication token from the token file.
     *
     * @return The authentication token as a string, or null if the file is missing.
     */
    private String readTokenFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("token.txt"))) {
            return reader.readLine();
        } catch (IOException e) {
            // file not found !! null
            return null;
        }
    }

    /**
     * Extracts the email address from a Google ID token.
     *
     * @param idToken The JWT ID token received from Google.  ( JSON Web Token )
     * @return The user's email if successfully extracted, otherwise null.
     */

    public String extractEmailFromIdToken(String idToken) {
        try {
            // split the JWT into its three parts
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid ID token format.");
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            JSONObject jsonPayload = new JSONObject(payload);

            // extract the email
            return jsonPayload.getString("email");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}

