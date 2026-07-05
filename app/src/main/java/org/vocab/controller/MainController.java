package org.vocab.controller;

import com.fxcontext.main.Context;
import com.fxcontext.message.Message;

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
        String path = filePathInput.getText();
        try(InputStream inputStream = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            VocabFileLoader vfl = new VocabFileLoader(vocabulary);
            vfl.loadVocab(reader);
        } catch (IOException ioe) {
            // ignore
        }

        Message message = Message.newBuilder()
            .setAction("start_quiz")
            .putExtra("vocabulary", vocabulary.toString())
            .build();
        context.broadcastMessage(message);
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