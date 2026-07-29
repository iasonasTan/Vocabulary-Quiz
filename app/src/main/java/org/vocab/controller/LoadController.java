package org.vocab.controller;

import com.je.core.JeLib;
import com.je.core.util.Bundle;
import com.je.io.configuration.Configuration;
import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.utils.MessageWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.vocab.util.MessageWindowShower;
import org.vocab.vocab.VocabFileLoader;
import org.vocab.vocab.Vocabulary;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.vocab.App.DARK_THEME;
import static org.vocab.App.SETTING_THEME_PATH;

@SuppressWarnings("unused")
public class LoadController extends VBox implements Initializable {
    private final Vocabulary vocabulary = new Vocabulary();

    @FXML
    public TextField wordPairInput, filePathInput;

    @FXML
    private Button startQuizButton;

    @FXML
    private CheckBox darkThemeCheckbox, reverseOrderCheckbox;

    @FXML
    private Label titleLabel;

    private final Context context;

    public LoadController(Context context) {
        this.context = context;
    }

    @SuppressWarnings("ConstantConditions")
    private void loadVersion() {
        try (InputStream inputStream = getClass().getResourceAsStream("/app_version.txt");
                InputStreamReader reader = new InputStreamReader(inputStream)) {
            String versionString = reader.readAllAsString().strip();
            String versionStringFormatted = String.format(" (Version %s)", versionString);
            titleLabel.setText(titleLabel.getText() + versionStringFormatted);
        } catch (IOException | NullPointerException e) {
            JeLib.console().error("Could not load version. " + e);
        }
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
                mw.close();
                startQuizButton.setDisable(false);
            });
            MessageWindowShower.showThemedMessageWindow(messageWindow);
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
            messageWindow.addAction("Ok", MessageWindow::close);
            MessageWindowShower.showThemedMessageWindow(messageWindow);
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
    //Gets called when the checkbox changes
    @FXML
    public void toggleDarkTheme(){
        Scene scene = darkThemeCheckbox.getScene();
        final boolean darkTheme = darkThemeCheckbox.isSelected();

        Bundle bundle = Bundle.builder().put(DARK_THEME, darkTheme).build();
        Configuration.storeBundle(SETTING_THEME_PATH, bundle);

        JeLib.console().log("Changing style in LoadController: darkTheme = " + darkTheme);

        scene.getRoot().getStylesheets().clear();
        if(darkTheme) {
            String style = Objects.requireNonNull(getClass().getResource("/css/dark_theme_style.css")).toExternalForm();
            scene.getRoot().getStylesheets().add(style);
        } else {
            scene.getRoot().setStyle("");
        }
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

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
        loadVersion();
        reverseOrderCheckbox.setSelected(vocabulary.isReverse());
    }

    @FXML
    private void reverseOrder() {
        vocabulary.setReverse(reverseOrderCheckbox.isSelected());
    }
}