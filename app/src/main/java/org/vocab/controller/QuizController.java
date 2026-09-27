package org.vocab.controller;

import com.je.core.JeLib;
import com.je.io.configuration.Configuration;
import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.receiver.MessageReceiver;
import com.jjfx.utils.MessageWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.vocab.App;
import org.vocab.debug.PairsWindow;
import org.vocab.util.Utils;
import org.vocab.vocab.AppStateIO;
import org.vocab.vocab.PairAndAnswers;
import org.vocab.vocab.Vocabulary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unused")
public final class QuizController extends VBox implements Initializable {
    @FXML
    public Label questionLabel;

    @FXML
    public CheckBox reverseCheckbox;

    @FXML
    public HBox answersLayout;

    private String pathToSaveState;
    private PairAndAnswers pair;
    private Vocabulary vocabulary;
    private final Context context;
    private final PairsWindow pairsWindow;

    public QuizController(Context context) {
        this.context = context;
        this.context.registerReceiver(new VocabularyInitializer());
        pairsWindow = new PairsWindow(context);
    }

    @FXML
    public void handleKeyPress(KeyEvent ke) {
        final KeyCode[] codes = {null, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4,
                KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9};
        final KeyCode[] codesNumpad = {null, KeyCode.NUMPAD1, KeyCode.NUMPAD2, KeyCode.NUMPAD3, KeyCode.NUMPAD4,
                KeyCode.NUMPAD5, KeyCode.NUMPAD6, KeyCode.NUMPAD7, KeyCode.NUMPAD8, KeyCode.NUMPAD9};
        final KeyCode code = ke.getCode();
        if(code.equals(KeyCode.L)) {
            Message message = Message.newBuilder()
                    .setAction("switch_logs")
                    .build();
            context.broadcastMessage(message);
        } else if (code.equals(KeyCode.V)) {
            pairsWindow.swap();
        } else {
            // Check if keycode is a number and if it is, check the answer
            JeLib.console().log("Counting to " + Math.min(codes.length, PairAndAnswers.sAnswersCountManager.getAnswersCount()));
            for(int i = 1; i<= Math.min(codes.length, PairAndAnswers.sAnswersCountManager.getAnswersCount()); i++) {
                if(code.equals(codes[i]) || code.equals(codesNumpad[i])) {
                    isAnswer(i-1);
                }
            }
        }
    }

    public void choiceAction(ActionEvent event) {
        String buttonText = ((Button)event.getSource()).getText(); // Get text of the pressed button
        int startIndex = buttonText.lastIndexOf('[')+1; // Index of the letter before the number plus one
        int endIndex = buttonText.lastIndexOf(']'); // Index of the letter after the number
        String number = buttonText.substring(startIndex, endIndex); // Get number inside brackets [1,2,3]
        isAnswer(Integer.parseInt(number)-1); // Call isAnswer() with parsed string to integer and subtracted by one. int(0,1,2)
    }

    private void isAnswer(int idx) {
        if(pair != null && pair.isAnswer(idx))
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

        Message dataMessage = Message.newBuilder()
                .setAction("visualizeData")
                .putExtra("data", vocabulary.toString())
                .build();
        context.broadcastMessage(dataMessage);
    }

    private void applyPair(PairAndAnswers pair) {
        this.pair = pair;
        questionLabel.setText(pair.getQuestion());
        for(int i=0; i<PairAndAnswers.sAnswersCountManager.getAnswersCount(); i++) {
            if(answersLayout.getChildren().get(i) instanceof Button button) {
                button.setText(pair.getAnswer(i) + "[" + (i+1) + "]");
            }
        }
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
        int buttonsCount = PairAndAnswers.sAnswersCountManager.getAnswersCount();
        for(int i=1; i<=buttonsCount; i++) {
            Button button = new Button("NULL");
            button.getStyleClass().add("choice");
            button.setOnAction(this::choiceAction);
            answersLayout.getChildren().add(button);
        }
    }

    @FXML
    private void save() {
        try {
            AppStateIO.write(new FileOutputStream(getFile()), vocabulary);
        } catch (IOException e) {
            Utils.handleException(context, e);
        }
    }

    private File getFile() {
        if(pathToSaveState == null) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            String fileName = String.format(
                    App.SAVED_STATES_FILE_FORMAT,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.YEAR),
                    UUID.randomUUID().toString().substring(0, 5)
            );
            String relPath = new File(App.SAVED_STATES_DIR_PATH, fileName).getPath();
            return Configuration.createConfigFile(relPath).toFile();
        } else {
            return new File(pathToSaveState);
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
                JeLib.console().log("Loading vocabulary and path.");
                pathToSaveState = message.getBundle().getString("savedState");
                vocabulary = new Vocabulary(true, message.getBundle().getString("vocabulary"));
                reverseCheckbox.setSelected(vocabulary.isReverse());
                nextQuestion();
            }
        }
    }

    @FXML
    public void openContributorLink(ActionEvent actionEvent) {
        Hyperlink hyperlink = (Hyperlink)actionEvent.getSource();
        String username = hyperlink.getId();
        Message openLink = Message.newBuilder()
                .setAction("open_link")
                .putExtra("link", "https://github.com/"+username)
                .build();
        context.broadcastMessage(openLink);
        hyperlink.setVisited(false);
    }
}