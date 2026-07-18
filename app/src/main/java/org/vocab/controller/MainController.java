package org.vocab.controller;

import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.utils.MessageWindow;

import org.vocab.Vocabulary;
import org.vocab.VocabFileLoader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MainController extends VBox {
    private final Vocabulary vocabulary = new Vocabulary();

    @FXML
    public TextField wordPairInput, filePathInput;

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
            MessageWindow messageWindow = new MessageWindow(
                    "Cannot start quiz.",
                    null, // TODO Pass main window (a future version of JFXWContext will be able to do it)
                    "Not enough pairs available!",
                    "You must add at least 3 pairs to start the quiz."
            );
            messageWindow.addAction("Ok", MessageWindow::closeWindow);
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
            System.out.println("[WARNING] File couldn't be loaded.");
        }
    }

    @FXML
    public void addWord() {
        try {
            vocabulary.add(wordPairInput.getText());
            wordPairInput.setText("");
        } catch (ArrayIndexOutOfBoundsException aioobe) {
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