package org.vocab.controller;

import com.fxcontext.main.Context;
import com.fxcontext.message.Message;

import org.vocab.Vocabulary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MainController extends VBox {
    private final Vocabulary vocabulary = new Vocabulary();

    @FXML
    public TextField wordPairInput;

    private final Context context;

    public MainController(Context context) {
        this.context = context;
    }

    @FXML
    public void startQuiz() {
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
}