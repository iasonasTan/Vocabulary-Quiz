module App {
    requires javafx.fxml;
    requires JavaFXContext;
    requires javafx.graphics;
    requires javafx.controls;

    exports org.vocab;
    exports org.vocab.controller;

    opens org.vocab.controller to javafx.fxml;
}