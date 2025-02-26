module org.example.cmpt370 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;


    opens org.example.cmpt370 to javafx.fxml;
    exports org.example.cmpt370;
}