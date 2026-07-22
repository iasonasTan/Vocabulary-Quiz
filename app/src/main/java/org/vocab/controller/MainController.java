package org.vocab.controller;

import com.je.core.JeLib;
import com.je.core.util.Bundle;
import com.je.io.configuration.Configuration;
import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.utils.MessageWindow;

import javafx.application.Platform;
import javafx.scene.Scene;
import org.vocab.App;
import org.vocab.vocab.Vocabulary;
import org.vocab.vocab.VocabFileLoader;

import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MainController extends VBox {
    private final Vocabulary vocabulary = new Vocabulary();

    @FXML
    public TextField wordPairInput, filePathInput;

    @FXML
    private Button startQuizButton;

    @FXML
    private CheckBox darkThemeCheckbox;

    private final Context context;

    public MainController(Context context) {
        this.context = context;
    }

    @FXML
    public void startQuiz() {
        if(vocabulary.hasMinimumWords()) {
            Message message = Message.newBuilder()
                    .setAction("start_quiz")
                    .putExtra("vocabulary", vocabulary.toString())
                    .build();
            context.broadcastMessage(message);
        } else {
            startQuizButton.setDisable(true);
            MessageWindow messageWindow = new MessageWindow(
                    "Cannot start quiz.",
                    context.getRootStage(),
                    "Not enough pairs available!",
                    "You must add at least 3 pairs to start the quiz."
            );
            messageWindow.addAction("Ok", mw -> {
                mw.closeWindow();
                startQuizButton.setDisable(false);
            });
            messageWindow.showWindow();
        }
    }

    // Separated the file-loading part of startQuiz() into its own method,
    // so the user can load files without starting the quiz.
    @FXML
    public void loadFromFile(){
        String path = filePathInput.getText();
        try(FileInputStream inputStream = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            VocabFileLoader vfl = new VocabFileLoader(vocabulary);
            vfl.loadVocab(reader);
            filePathInput.setText("");
        }catch (IOException ioe) {
            MessageWindow messageWindow = new MessageWindow(
                    "File not found.",
                    context.getRootStage(),
                    "Could not find file.",
                    "The file you entered does not exist. Double-Check the file path.");
            messageWindow.addAction("Ok", MessageWindow::closeWindow);
            messageWindow.showWindow();
            JeLib.console().warn("File couldn't be loaded.");
        }
    }

    @FXML
    public void browseFile() {
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Select Vocabulary File");

       java.io.File selectedFile = fileChooser.showOpenDialog(context.getRootStage());

       if (selectedFile != null) {
        filePathInput.setText(selectedFile.getAbsolutePath());
       }
    }

    //Lets the user manually choose between light and dark theme
    //Get's called when the checkbox changes
    @FXML
    public void toggleDarkTheme(){
        Scene scene = darkThemeCheckbox.getScene();
        final boolean darkTheme = darkThemeCheckbox.isSelected();

        Bundle bundle = Bundle.builder().put(App.DARK_THEME, darkTheme).build();
        Configuration.storeBundle(App.SETTING_THEME_PATH, bundle);

        JeLib.console().log("Changing style in MainController: darkTheme = " + darkTheme);

        scene.getStylesheets().clear();
        if(darkTheme) {
            String style = Objects.requireNonNull(getClass().getResource("/style/dark_theme_style.css")).toExternalForm();
            scene.getRoot().getStylesheets().add(style);
        } else {
            scene.getRoot().setStyle("");
        }

        scene.getRoot().applyCss();
        scene.getRoot().layout();
    }

    @FXML
    public void addWord() {
        try {
            vocabulary.add(wordPairInput.getText());
            wordPairInput.setText("");
        } catch (ArrayIndexOutOfBoundsException _) {
            // Just let 'em know
            JeLib.console().warn("Pair couldn't be added.");
        }
    }

    @FXML
    public void quit() {
        Message message = Message.newBuilder()
            .setAction("abort_app")
            .build();
        context.broadcastMessage(message);
    }
}