package org.example.cmpt370;

import javafx.scene.layout.StackPane;

/** Class to set up and connect each of the three
 * MVC pieces. Only instantiated by Main for program execution */
public class Setup extends StackPane {

    public Setup() {
        // Instantiate each class
        Model m = new Model();
        View v = new View();
        Controller c = new Controller(v);

        // Connect MVC layout
        m.addSubscriber(v);
        v.setModel(m);
        c.setModel(m);

        // Ensures View receives inputs
        v.requestFocus();
        this.getChildren().addAll(v);
    }
}
