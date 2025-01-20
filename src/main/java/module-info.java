module org.example.cmpt370 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cmpt370 to javafx.fxml;
    exports org.example.cmpt370;
}