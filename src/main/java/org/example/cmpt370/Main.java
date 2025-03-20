package org.example.cmpt370;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** This class runs the application */
public class Main extends Application {
    @Override
    public void start(Stage stage) {

        Setup setup = new Setup();
        Scene scene = new Scene(setup, 1200, 800);      // dimensions
        scene.getStylesheets().addAll("buttonstyle.css");   // apply styling globally
        stage.setTitle("Usask GeoGuessr");
        stage.setResizable(false);

        // Could also remove default application / OS level exit button
        // in favour of just using the exit button
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}