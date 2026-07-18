package org.vocab.controller;

import java.util.Optional;

import org.vocab.Vocabulary;

import com.fxcontext.main.Context;
import com.fxcontext.message.Message;
import com.fxcontext.receiver.MessageReceiver;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public final class QuizController extends VBox {
    @FXML
    public Label questionLabel;

    @FXML
    public Button choice1, choice2, choice3;

    private Vocabulary.PairAndAnswers pair;
    private Vocabulary vocabulary;
    private boolean reverse;
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
            default:
                // Ignore all other key codes
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
        Optional<Vocabulary.PairAndAnswers> pairOpt = vocabulary.randomPairAndAnswers(reverse);
        pairOpt.ifPresent(pairAndAnswers -> {
            this.pair = pairAndAnswers;
            questionLabel.setText(pairAndAnswers.getQuestion());
            choice1.setText(pairAndAnswers.answers().get(0) + "[1]");
            choice2.setText(pairAndAnswers.answers().get(1) + "[2]");
            choice3.setText(pairAndAnswers.answers().get(2) + "[3]");
        });
    }

    private final class VocabularyInitializer implements MessageReceiver {
        @Override
        public void onReceive(Message message) {
            if(message.getAction().equals("initialize_vocabulary")) {
                vocabulary = new Vocabulary(message.getBundle().getString("vocabulary"));
                reverse = Boolean.parseBoolean(message.getBundle().getString("reverse"));
                nextQuestion();
            }
        }
    }
}