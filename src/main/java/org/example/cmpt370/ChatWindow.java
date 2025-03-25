package org.example.cmpt370;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;

/** Display class for the chat function
 * Needs to see model/server to get messages
 * Needs to be seen by the view to be displayed */
public class ChatWindow extends StackPane {
    // ChatWindow is a StackPane, holding a VBox with a scroll pane on top
    // for messages and a textEntry on the bottom to send a message
    private VBox messages;
    private TextField entry;
    private Button send;
    // needs to see model and be seen by the view -> Setup

    public ChatWindow() {
        // the View will toggle this so we can hide the chat window during play
        this.setPrefSize(250, 300);
        this.setMinSize(250, 300);
        this.setMaxSize(250, 300);
        this.setVisible(false);

        this.messages = new VBox(10);
        this.messages.setPadding(new Insets(5));
        this.messages.setPrefWidth(230);
        this.messages.setAlignment(Pos.TOP_LEFT);

        ScrollPane chat = new ScrollPane(this.messages);
        chat.setFitToWidth(true);
        chat.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // message entry
        this.entry = new TextField();
        this.entry.setPrefWidth(180);
        this.send = new Button("Send");
        this.send.setPrefSize(70, 50);
        this.send.setStyle("-fx-font-size: 14px;");
        HBox entryField = new HBox(10, this.entry, this.send);
        entryField.setPadding(new Insets(5));
        entryField.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(10, chat, entryField);
        container.setPrefSize(250, 300);
        container.setPadding(new Insets(5));

        this.getChildren().add(container);
        this.show();
    }

    /** Adds a message to itself for display
     * @param msg the String message
     * @param outgoing signals owner of the message (true = us) */
    public void addMessage(String msg, boolean outgoing) {

        Text text = new Text(msg);
        text.setFont(Font.font("Segoe UI This", FontWeight.NORMAL, 14));
        //text.setFill(Color.WHITE);

        // Wrap text in a TextFlow for formatting
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));
        textFlow.setMaxWidth(225);
        textFlow.setStyle("-fx-background-radius: 10px;");

        if (outgoing) {
            // our message
            textFlow.setStyle("-fx-background-color: #0A6A42;");
            textFlow.setTextAlignment(TextAlignment.RIGHT);
        } else {
            // other players message
            textFlow.setStyle("-fx-background-color: #444444;");
            textFlow.setTextAlignment(TextAlignment.LEFT);
        }

        this.messages.getChildren().add(textFlow);
    }


    /** Shows or hides the Chat window */
    public void show() {
        this.setVisible(true);
    }
    public void hide() {
        this.setVisible(false);
    }

}
