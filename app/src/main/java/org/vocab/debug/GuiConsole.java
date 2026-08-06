package org.vocab.debug;

import com.je.core.JeLib;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;

public class GuiConsole extends Stage {
    private boolean mVisible = false;
    private final TextArea mConsoleTextArea;

    public GuiConsole() {
        var layoutGroup = new StackPane();
        layoutGroup.getStyleClass().add("console-panel");

        mConsoleTextArea = new TextArea();
        mConsoleTextArea.getStyleClass().add("console-text-area");
        mConsoleTextArea.setEditable(false);
        layoutGroup.getChildren().add(mConsoleTextArea);

        Scene scene = new Scene(layoutGroup, 500, 500);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/console_theme_style.css")).toExternalForm()
        );

        setScene(scene);
        setWidth(500);
        setHeight(500);
        setX(500);
        setY(0);
        setTitle("Vocabulary Quiz - Developer Console");

        widthProperty().addListener((_, _, newValue) ->
                mConsoleTextArea.setPrefWidth(newValue.intValue()));
        heightProperty().addListener((_, _, newValue) ->
                mConsoleTextArea.setPrefHeight(newValue.intValue()));

        redirectSystemStreams();
    }

    public void swap() {
        mVisible = !mVisible;
        if(mVisible) {
            showWindow();
        } else {
            hideWindow();
        }
    }

    public void showWindow() {
        if (isShowing()) {
            toFront();
        } else {
            show();
            JeLib.console().log("Developer tools initialized.");
        }
    }

    public void hideWindow() {
        hide();
    }

    private void redirectSystemStreams() {
        OutputStream systemOutput = System.out;
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // Write in GUI console
                String charToWrite = String.valueOf((char)b);
                mConsoleTextArea.appendText(charToWrite);
                // Write in system console
                systemOutput.write(b);
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}
