package org.vocab.controller;

import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.utils.MessageWindow;

import javafx.scene.Scene;
import org.vocab.Vocabulary;
import org.vocab.VocabFileLoader;

import javafx.stage.FileChooser;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try(InputStream inputStream = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            VocabFileLoader vfl = new VocabFileLoader(vocabulary);
            vfl.loadVocab(reader);
            filePathInput.setText("");
        }catch (IOException ioe){
            MessageWindow messageWindow = new MessageWindow(
                    "File not found.",
                    context.getRootStage(),
                    "Could not find file.",
                    "The file you entered does not exist. Double-Check the file path.");
            messageWindow.addAction("Ok", MessageWindow::closeWindow);
            messageWindow.showWindow();
            System.out.println("[WARNING] File couldn't be loaded.");

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
    @FXML
    public void toggleDarkTheme(){
        Scene scene = darkThemeCheckbox.getScene();
        scene.getStylesheets().clear();
        if(darkThemeCheckbox.isSelected()){
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/dark_theme_style.css")).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/light_theme_style.css")).toExternalForm());
        }
    }

    @FXML
    public void addWord() {
        try {
            vocabulary.add(wordPairInput.getText());
            wordPairInput.setText("");
        } catch (ArrayIndexOutOfBoundsException _) {
            // Just let 'em know
            System.out.println("[WARNING] Pair couldn't be added.");
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