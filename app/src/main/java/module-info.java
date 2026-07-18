module App {
    requires JeJFX;

    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires jdk.jfr;

    exports org.vocab;
    exports org.vocab.controller;

    opens org.vocab.controller to javafx.fxml;
}