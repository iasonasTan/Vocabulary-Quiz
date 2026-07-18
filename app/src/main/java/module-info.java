module App {
    requires transitive javafx.fxml;
    requires transitive JavaFXContext;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires java.desktop;
    exports org.vocab;
    exports org.vocab.controller;

    opens org.vocab.controller to javafx.fxml;
}