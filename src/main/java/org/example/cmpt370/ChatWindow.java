package org.example.cmpt370;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.Stage;

/** Display class for the chat function
 * Needs to see model/server to get messages
 * Needs to be seen by the view to be displayed */
public class ChatWindow extends StackPane {

    private VBox messages;          // stack of all messages
    private TextField entry;        // entry field to type outgoing
    private Button send;            // button to send
    private Model model;        // takes model just to set send button handler

    /** Constructs the ChatWindow, launching it as a separate window from View
     * @param m take the model as an arg to set up handler */
    public ChatWindow(Model m) {
        // the View will toggle this so we can hide the chat window during play
        this.setPrefSize(250, 300);
        this.setMinSize(250, 300);
        this.setMaxSize(250, 300);
        this.model = m;

        this.messages = new VBox(10);
        this.messages.setPadding(new Insets(5));
        this.messages.setPrefWidth(230);
        this.messages.setAlignment(Pos.TOP_LEFT);

        ScrollPane chat = new ScrollPane(this.messages);
        chat.setFitToWidth(true);
        chat.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(chat, Priority.ALWAYS);

        // message entry
        this.entry = new TextField();
        this.entry.setPrefWidth(180);
        this.entry.setOnAction(event -> this.send.fire());  // uses Enter key to trigger send

        this.send = new Button("Send");
        this.send.setPrefSize(70, 15);
        this.send.setStyle("-fx-font-size: 14px;");
        this.send.setOnAction(event -> {
            if (!this.entry.getText().trim().isEmpty()) {
                this.model.sendMessage(this.entry.getText());
                this.entry.clear();
                // set scroll to bottom
                Platform.runLater(() -> chat.setVvalue(1.0));
            }
        });
        HBox entryField = new HBox(10, this.entry, this.send);
        entryField.setPadding(new Insets(5));
        entryField.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(10, chat, entryField);
        container.setPrefSize(250, 300);
        container.setPadding(new Insets(5));

        this.getChildren().add(container);

        // Display in a separate window from View
        Scene scene = new Scene(this, 250, 300);
        scene.getStylesheets().addAll("buttonstyle.css");

        Stage chatStage = new Stage();
        chatStage.setScene(scene);
        chatStage.setTitle("Chat");
        chatStage.show();
    }

    /** Adds a message to itself for display
     * @param msg the String message
     * @param outgoing signals owner of the message (true = us) */
    public void addMessage(String msg, boolean outgoing) {

        Text text = new Text(msg);
        text.setFont(Font.font("Segoe UI This", FontWeight.NORMAL, 14));

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

    /** Close chat window */
    public void exitChatWindow() {
        Platform.exit();
    }

}
