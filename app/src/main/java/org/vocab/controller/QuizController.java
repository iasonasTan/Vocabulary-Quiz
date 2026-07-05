package org.vocab.controller;

import com.fxcontext.main.Context;
import com.fxcontext.message.Message;
import com.fxcontext.receiver.MessageReceiver;

import org.vocab.Vocabulary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

public final class QuizController extends VBox {
    @FXML
    public Label questionLabel;

    @FXML
    public Button choice1, choice2, choice3;

    private Vocabulary.DataPair pair;
    private Vocabulary vocabulary;
    private final Context context;

    public QuizController(Context context) {
        this.context = context;
        this.context.registerReceiver(new VocabularyInitializer());
    }

    @FXML
    public void choice0action() {
        if(pair != null && pair.isAnswer0()) { nextQuestion(); }
    }

    @FXML
    public void choice1action() {
        if(pair != null && pair.isAnswer1()) { nextQuestion(); }
    }

    @FXML
    public void choice2action() {
        if(pair != null && pair.isAnswer2()) { nextQuestion(); }
    }

    @FXML
    public void quit() {
        Message message = Message.newBuilder()
            .setAction("abort_app")
            .build();
        context.broadcastMessage(message);
    }

    private void nextQuestion() {
        Optional<Vocabulary.DataPair> pairOpt = vocabulary.getRandom();
        pairOpt.ifPresent(pair -> {
            this.pair = pair;
            questionLabel.setText(pair.question());
            choice1.setText(pair.answers()[0]);
            choice2.setText(pair.answers()[1]);
            choice3.setText(pair.answers()[2]);
        });
    }

    private final class VocabularyInitializer implements MessageReceiver {
        @Override
        public void onReceive(Message message) {
            if(message.getAction().equals("initialize_vocabulary")) {
                vocabulary = new Vocabulary(message.getBundle().getString("vocabulary"));
                nextQuestion();
            }
        }
    }
}