package org.vocab.controller;

import com.je.core.JeLib;
import com.je.core.util.Bundle;
import com.je.io.configuration.Configuration;
import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.utils.MessageWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.vocab.App;
import org.vocab.util.MWUtils;
import org.vocab.vocab.AppStateIO;
import org.vocab.vocab.VocabFileLoader;
import org.vocab.vocab.Vocabulary;

import java.io.*;
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

    @FXML
    private VBox loadButtons;

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

    private Button constructImageButton(String path) {
        Button button = new Button();
        String url = Objects.requireNonNull(
                getClass().getResource("/icon/"+path)).toExternalForm();
        ImageView imageView = new ImageView(url);
        imageView.setFitWidth(23);
        imageView.setFitHeight(23);
        button.setGraphic(imageView);
        return button;
    }

    private void addGui(boolean darkTheme, File[] files) {
        boolean index = true;
        String[] cssLight = new String[]{
                "-fx-background-color: #d1d1d1",
                "-fx-background-color: #ffffff"
        };
        String[] cssDark = new String[]{
                "-fx-background-color: #939393;",
                "-fx-background-color: #454545;"
        };

        loadButtons.getChildren().clear();
        for(File file: files) {
            HBox hbox = new HBox(10);
            hbox.setPadding(new Insets(5, 10, 5, 10));
            // noinspection all : false-positive
            hbox.setStyle(darkTheme?cssDark[(index = !index) ?0:1]:cssLight[(index = !index) ?0:1]);

            Button button = new Button(file.getName());
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            Button deleteButton = constructImageButton(darkTheme?"delete-dark.png":"delete.png");
            Button renameButton = constructImageButton(darkTheme?"edit-dark.png"  :"edit.png");

            button.setOnAction(new IOButtonEvent(IOButtonEvent.Type.LOAD, file));
            deleteButton.setOnAction(new IOButtonEvent(IOButtonEvent.Type.DELETE, file));
            renameButton.setOnAction(new IOButtonEvent(IOButtonEvent.Type.RENAME, file));

            hbox.getChildren().addAll(button, region, deleteButton, renameButton);
            loadButtons.getChildren().add(hbox);
        }
    }

    private final class IOButtonEvent implements EventHandler<ActionEvent> {
        private final Type mType;
        private final File mFile;

        private IOButtonEvent(Type type, File filePath) {
            mType = type;
            mFile = filePath;
        }

        /**
         * Invoked when a specific event of the type for which this handler is
         * registered happens.
         *
         * @param event the event which occurred
         */
        @Override
        public void handle(ActionEvent event) {
            try {
                switch (mType) {
                    case DELETE: delete(); break;
                    case RENAME: rename(); break;
                    case LOAD: load(); break;
                }
                loadSavedStates(Configuration.loadBundle(SETTING_THEME_PATH).getBoolean(DARK_THEME, false));
            } catch (IOException ioe) {
                JeLib.console().exception(ioe);
                JeLib.console().error("Could not load state. " + ioe);
                MessageWindow messageWindow = new MessageWindow(
                        "Vocabulary Quiz - Error",
                        context.getRootStage(),
                        "Cannot load state.",
                        "An error occurred while trying to load this state. Please try again."
                );
                messageWindow.addActionOk();
                MWUtils.showThemed(messageWindow);
            }
        }

        private void load() throws FileNotFoundException {
            AppStateIO.load(context, new FileInputStream(mFile), vocabulary);
            startQuiz();
        }

        private void rename() {
            // TODO: Waiting for JeJavaFXUtils:InputWindow
            String newName = "unknown";

            String oldName = mFile.getName();
            String path = mFile.getAbsolutePath().replace(oldName, "");

            boolean result = mFile.renameTo(new File(path, newName));
            JeLib.console().log("File '"+ mFile +"' was " + (result?"":"not") + " renamed!");
        }

        private void delete() {
            if(mFile.exists()) {
                boolean result = mFile.delete();
                JeLib.console().log("File '" + mFile + "' was " + (result ? "" : "not") + " deleted!");
            }
        }

        public enum Type {
            DELETE,
            RENAME,
            LOAD,
        }
    }

    private void loadSavedStates(boolean darkTheme) {
        try {
            JeLib.console().log("Loading saved states...");
            File path = Configuration.createConfigDir(App.SAVED_STATES_DIR_PATH).toFile();
            File[] files = path.listFiles();
            if(files == null)
                return;

            addGui(darkTheme, files);
        } catch (Exception ioe) {
            JeLib.console().error("Could not load states.");
            JeLib.console().exception(ioe);
            MessageWindow messageWindow = new MessageWindow(
                    "Vocabulary Quiz - Error",
                    context.getRootStage(),
                    "Cannot load states.",
                    "An error occurred while trying to load saved states. Please try again."
            );
            messageWindow.addActionOk();
            MWUtils.showThemed(messageWindow);
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
            MWUtils.showThemed(messageWindow);
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
                    "The file you entered does not exist. Double-Check the file path."
            );
            messageWindow.addActionOk();
            MWUtils.showThemed(messageWindow);
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

        loadSavedStates(darkTheme);

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
        loadSavedStates(Configuration.loadBundle(SETTING_THEME_PATH).getBoolean(DARK_THEME, false));
        reverseOrderCheckbox.setSelected(vocabulary.isReverse());
    }

    @FXML
    private void reverseOrder() {
        vocabulary.setReverse(reverseOrderCheckbox.isSelected());
    }

    @FXML
    private void showReverseOrderHint() {
        MessageWindow messageWindow = new MessageWindow(
                "Vocabulary Quiz - Hint",
                context.getRootStage(),
                "Reverse Order",
                "Shows loaded pairs in reverse order.\nYou can change this later."
        );
        messageWindow.addActionOk();
        MWUtils.showThemed(messageWindow);
    }

    @FXML
    private void showManualPairAddingHints() {
        MessageWindow messageWindow = new MessageWindow(
                "Vocabulary Quiz - Hint",
                context.getRootStage(),
                "Add pair manually",
                "Add a pair you typed above manually.\nType key=value and press the button."
        );
        messageWindow.addActionOk();
        MWUtils.showThemed(messageWindow);
    }

    @FXML
    private void showFileLoadingHints() {
        MessageWindow messageWindow = new MessageWindow(
                "Vocabulary Quiz - Hint",
                context.getRootStage(),
                "Load words from file",
                "Loads words from a file.\nFormat of file:\nkey1=value1\nkey2=value2\n...\nYou can choose a file by clicking 'Browse'"
        );
        messageWindow.setDimension(new Dimension2D(350, 300));
        messageWindow.addActionOk();
        MWUtils.showThemed(messageWindow);
    }
}