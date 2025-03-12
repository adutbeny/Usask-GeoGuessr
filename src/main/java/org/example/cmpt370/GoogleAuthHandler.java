package org.example.cmpt370;

import javafx.animation.AnimationTimer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.Executors;

public class GoogleAuthHandler {

    private Process pythonServerProcess;

    //flag to check if python server is already running
    private boolean isPythonServerStarted = false;

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

            // unknown debuggin steps which could most likely be removed
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

    public void stopPythonServer() {
        if (pythonServerProcess != null && pythonServerProcess.isAlive()) {
            pythonServerProcess.destroy();
            System.out.println("Python server stopped.");
            isPythonServerStarted = false;
        }
    }

    public void openGoogleSignInPage() {
        try {
            // open the Google Sign-In page ( check to make sure port # matches everywhere )
            Desktop.getDesktop().browse(new URI("http://localhost:63347/googleSignIN.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startTokenChecker(Runnable onTokenReceived) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                String token = readTokenFromFile();
                if (token != null) {
                    // token received, handle it
                    System.out.println("Google Token: " + token);   //print to console for debugging purposes
                    stopPythonServer();                             //make sure to stop server........
                    this.stop();
                    onTokenReceived.run();
                }
            }
        };
        timer.start();
    }

    private String readTokenFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("token.txt"))) {
            return reader.readLine();
        } catch (IOException e) {
            // file not found
            return null;
        }
    }
}