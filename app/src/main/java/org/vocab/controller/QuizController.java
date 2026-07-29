package org.vocab.controller;

import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.receiver.MessageReceiver;
import org.vocab.vocab.Vocabulary;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

@SuppressWarnings("unused")
public final class QuizController extends VBox {
    @FXML
    public Label questionLabel;

    @FXML
    public Button choice1, choice2, choice3;

    private Vocabulary.PairAndAnswers pair;
    private Vocabulary vocabulary;
    private final Context context;

    public QuizController(Context context) {
        this.context = context;
        this.context.registerReceiver(new VocabularyInitializer());
    }

    @FXML
    public void handleKeyPress(KeyEvent ke) {
        switch(ke.getCode()) {
            case KeyCode.DIGIT1:
            case KeyCode.NUMPAD1:
                choice0action();
                break;
            case KeyCode.DIGIT2:
            case KeyCode.NUMPAD2:
                choice1action();
                break;
            case KeyCode.DIGIT3:
            case KeyCode.NUMPAD3:
                choice2action();
                break;
        }
    }

    @FXML
    public void choice0action() {
        if(pair != null && pair.isAnswer0())
            nextQuestion();
    }

    @FXML
    public void choice1action() {
        if(pair != null && pair.isAnswer1())
            nextQuestion();
    }

    @FXML
    public void choice2action() {
        if(pair != null && pair.isAnswer2())
            nextQuestion();
    }

    @FXML
    public void back() {
        Message message = Message.newBuilder()
            .setAction("main")
            .build();
        context.broadcastMessage(message);
    }

    private void nextQuestion() {
        Optional<Vocabulary.PairAndAnswers> pairOpt = vocabulary.randomPairAndAnswers();
        pairOpt.ifPresent(pair -> {
            this.pair = pair;
            questionLabel.setText(pair.getQuestion());
            choice1.setText(pair.answers()[0] + "[1]");
            choice2.setText(pair.answers()[1] + "[2]");
            choice3.setText(pair.answers()[2] + "[3]");
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