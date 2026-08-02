package org.vocab.visualizer;

import com.jjfx.context.Context;
import com.jjfx.message.Message;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class VisualizerWindow extends Stage {
    private boolean mVisible = false;
    private final TextArea mTextArea2, mTextArea1;

    public VisualizerWindow(Context context) {
        context.registerReceiver(this::onReceive);
        initWindow(context);

        VBox vbox = new VBox();
        mTextArea1 = buildTextArea("#00ff10", vbox);
        mTextArea2 = buildTextArea("#ff0004", vbox);
        setScene(new Scene(vbox));

        widthProperty().addListener((_, _, newValue) -> {
            mTextArea1.setPrefWidth(newValue.doubleValue());
            mTextArea2.setPrefWidth(newValue.doubleValue());
        });

        heightProperty().addListener((_, _, newValue) -> {
            mTextArea1.setPrefHeight(newValue.doubleValue());
            mTextArea2.setPrefHeight(newValue.doubleValue());
        });
    }

    private void initWindow(Context context) {
        initOwner(context.getRootStage());
        setAlwaysOnTop(true);
        setTitle("Learning Visualizer");
        setX(0);
        setY(0);
        setWidth(500);
        setHeight(500);
    }

    private TextArea buildTextArea(String hexColor, Pane parent) {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(getWidth());
        textArea.setPrefHeight(getHeight()/2);
        textArea.setWrapText(true);

        textArea.setStyle(
                "-fx-control-inner-background: " + hexColor + "; " +
                "-fx-background-color: " + hexColor + ";"
        );

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        parent.getChildren().add(scrollPane);
        return textArea;
    }

    public void onReceive(Message message) {
        if(message.getAction().equals("visualizeData")) {
            String words = message.getBundle()
                    .getString("data")
                    .replace(",", "\n");

            List<String> lines = words.lines().toList();
            mTextArea1.setText(
                    lines.subList(0, lines.size()/2)
                        .toString()
                        .replace("]", "")
                        .replace("[", "")
                        .replace(",", "\n")
            );
            mTextArea2.setText(
                    lines.subList(lines.size()/2, lines.size()-1)
                        .toString()
                        .replace("]", "")
                        .replace("[", "")
                        .replace(",", "\n")
            );
        }
    }

    public void swap() {
        if(mVisible) hide();
        else show();
        mVisible = !mVisible;
    }
}