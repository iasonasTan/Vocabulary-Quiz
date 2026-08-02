package org.vocab.controller;

import com.je.io.configuration.Configuration;
import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.receiver.MessageReceiver;
import com.jjfx.utils.MessageWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.vocab.App;
import org.vocab.util.Utils;
import org.vocab.vocab.AppStateIO;
import org.vocab.vocab.PairAndAnswers;
import org.vocab.vocab.Vocabulary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public final class QuizController extends VBox implements Initializable {
    @FXML
    public Label questionLabel;

    @FXML
    public Button choice1, choice2, choice3;

    @FXML
    public CheckBox reverseCheckbox;

    private PairAndAnswers pair;
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
        if(pair != null && pair.isAnswer(0))
            nextQuestion();
    }

    @FXML
    public void choice1action() {
        if(pair != null && pair.isAnswer(1))
            nextQuestion();
    }

    @FXML
    public void choice2action() {
        if(pair != null && pair.isAnswer(2))
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
        Optional<PairAndAnswers> pairOpt = vocabulary.randomPairAndAnswers();
        pairOpt.ifPresent(this::applyPair);
    }

    private void applyPair(PairAndAnswers pair) {
        this.pair = pair;
        questionLabel.setText(pair.getQuestion());
        choice1.setText(pair.getAnswer(0) + "[1]");
        choice2.setText(pair.getAnswer(1) + "[2]");
        choice3.setText(pair.getAnswer(2) + "[3]");
    }

    @FXML
    private void reverseOrder() {
        vocabulary.setReverse(reverseCheckbox.isSelected());
        applyPair(pair);
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void save() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        String fileName = String.format(
                App.SAVED_STATES_FILE_FORMAT,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
        );
        String absPath = new File(App.SAVED_STATES_DIR_PATH, fileName).getPath();
        File file = Configuration.createConfigFile(absPath).toFile();
        try {
            AppStateIO.write(new FileOutputStream(file), vocabulary);
        } catch (IOException e) {
            Utils.handleException(context, e);
        }
    }

    @FXML
    private void explainSave() {
        MessageWindow messageWindow = new MessageWindow(
                "Vocabulary Quiz - Hint",
                context.getRootStage(),
                "Save app state.",
                "Save the current words and scores.\nYou will be able to load them again in home screen."
        );
        messageWindow.addActionOk();
        Utils.showThemed(messageWindow);
    }

    private final class VocabularyInitializer implements MessageReceiver {
        @Override
        public void onReceive(Message message) {
            if(message.getAction().equals("initialize_vocabulary")) {
                vocabulary = new Vocabulary(message.getBundle().getString("vocabulary"));
                reverseCheckbox.setSelected(vocabulary.isReverse());
                nextQuestion();
            }
        }
    }
}