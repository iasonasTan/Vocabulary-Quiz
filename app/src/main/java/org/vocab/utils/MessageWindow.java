package org.vocab.utils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class MessageWindow extends Stage {
    private final VBox mMainLayout = new VBox();
    private final HBox mButtonsLayout = new HBox();

    public MessageWindow(String title, Stage parent, String message, String description) {
        addText(message, description);
        initOwner(parent);
        setAlwaysOnTop(true);
        setTitle(title);
        mMainLayout.setAlignment(Pos.CENTER);
        mMainLayout.setSpacing(3);
    }

    private void addText(String message, String description) {
        Label titleLabel = new Label(message);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: large;");
        mMainLayout.getChildren().add(titleLabel);

        Label descriptionLabel = new Label(description);
        mMainLayout.getChildren().add(descriptionLabel);
    }

    public void addAction(String text, MessageWindowListener action) {
        Button button = new Button(text);
        button.setOnAction(_ -> action.onOk(this));
        mButtonsLayout.getChildren().add(button);
    }

    public void showWindow() {
        mMainLayout.getChildren().add(mButtonsLayout);
        setScene(new Scene(mMainLayout, 0, 0));
        sizeToScene();
        show();
    }

    public void closeWindow() {
        close();
    }

    public interface MessageWindowListener {
        void onOk(MessageWindow window);
    }
}
