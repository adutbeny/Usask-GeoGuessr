package org.example.cmpt370;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** This class runs the application */
public class Main extends Application {
    @Override
    public void start(Stage stage) {

        Setup setup = new Setup();
        Scene scene = new Scene(setup, 1200, 800);
        scene.getStylesheets().addAll("buttonstyle.css");   // adds button styling
        stage.setTitle("Usask GeoGuessr");
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}