package org.example.cmpt370;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.animation.AnimationTimer;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class GoogleAuthHandler {

    private Process pythonServerProcess;
    private boolean isPythonServerStarted = false;

    public void startPythonServer() {
        if (isPythonServerStarted) {
            System.out.println("Python server is already running.");
            return;
        }

        try {
            // path to the Python server
            String pythonScriptPath = "src/main/python/server.py";

            // start the Python server
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            processBuilder.redirectErrorStream(true);
            pythonServerProcess = processBuilder.start();

            // Print Python server output to the console (for debugging)
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
            isPythonServerStarted = true; // Set the flag to true
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
            // open the Google Sign-In page
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
                    // Token received, handle it
                    System.out.println("Google Token: " + token);
                    stopPythonServer();
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
            // File not found or other error
            return null;
        }
    }

    public void startAuthServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(63343), 0);
            server.createContext("/auth", new AuthHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Auth server running at http://localhost:63343");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read and parse the JSON request body
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    requestBody.append(line);
                }
                bufferedReader.close();

                // Extract the token
                String json = requestBody.toString();
                String token = json.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");

                System.out.println("Received Google Token: " + token);

                // Write the token to a file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("token.txt"))) {
                    writer.write(token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Add CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            // Send a response
            String response = "Token received!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}