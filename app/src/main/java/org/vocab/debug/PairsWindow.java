package org.vocab.debug;

import com.jjfx.context.Context;
import com.jjfx.message.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Function;

public class PairsWindow extends Stage {
    private boolean mVisible = false;
    private final ColoredTextArea mTextArea2, mTextArea1;

    public PairsWindow(Context context) {
        context.registerReceiver(this::onReceive);
        initWindow(context);

        VBox vbox = new VBox();

        var title = new Label("Green: Can be asked. Red: Won't be asked.");
        title.setPrefHeight(20);
        vbox.getChildren().add(title);

        var areasPane = new VBox();
        areasPane.getChildren().addAll(
                mTextArea1 = new ColoredTextArea("#00ff10"),
                mTextArea2 = new ColoredTextArea("#ff0004")
        );

        ScrollPane scrollPane = new ScrollPane(areasPane);
        scrollPane.setStyle("-fx-control-inner-background: transparent; -fx-background-color: transparent; ");
        vbox.getChildren().add(scrollPane);

        setScene(new Scene(vbox));

        widthProperty().addListener((_, _, newValue) -> {
            mTextArea1.setPrefWidth(newValue.doubleValue());
            mTextArea2.setPrefWidth(newValue.doubleValue());
            title.setPrefWidth(newValue.doubleValue());
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

    private static final class ColoredTextArea extends TextArea {
        private Text text = new Text();

        public ColoredTextArea(String hexColor) {
            setEditable(false);
            setPrefWidth(getWidth());
            setPrefHeight(getHeight()/2);
            setWrapText(true);

            text.textProperty().bind(this.textProperty());
            text.layoutBoundsProperty().addListener((_, _, newValue) ->
                    setPrefHeight(newValue.getHeight()));

            setStyle(
                "-fx-control-inner-background: " + hexColor + "; " +
                "-fx-background-color: " + hexColor + ";"
            );
        }

        public void setTextAndResize(String t) {
            setText(t);
        }
    }

    public void onReceive(Message message) {
        if(message.getAction().equals("visualizeData")) {
            String words = message.getBundle()
                    .getString("data");

            Function<List<String>, String> stringCreator = splitPairs -> splitPairs.stream()
                    .map(s -> s+"\n")
                    .toList()
                    .toString()
                    .replace("]", "") // Remove List.toString stuff
                    .replace("[", "") // Remove List.toString stuff
                    .replace(",", "")
                    .replace(" ", "");

            List<String> lines = words.lines().toList();
            mTextArea1.setTextAndResize(
                    stringCreator.apply(lines.subList(0, lines.size()/2))
            );
            mTextArea2.setTextAndResize(
                    stringCreator.apply(lines.subList(lines.size()/2, lines.size()-1))
            );
        }
    }

    public void swap() {
        if(mVisible) hide();
        else show();
        mVisible = !mVisible;
    }
}