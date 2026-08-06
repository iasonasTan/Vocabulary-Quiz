package org.vocab;

import com.je.core.Console;
import com.je.core.JeLib;
import com.je.core.util.Bundle;
import com.je.io.configuration.Configuration;
import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.receiver.MessageReceiver;
import com.jjfx.utils.Disposable;
import com.jjfx.utils.MessageWindow;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.vocab.debug.GuiConsole;
import org.vocab.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App extends Application implements Context {
    // Appearance & Theming
    public static final String SETTING_THEME_PATH = "dark_theme.dat";
    public static final String DARK_THEME = "org.vocab.settings.darkTheme";
    // Saved states & Logic
    public static final String SAVED_STATES_DIR_PATH = "saved_states";
    public static final String SAVED_STATES_FILE_FORMAT = "state-%s-%s-%s-%s.dat";

    public static final int STAGE_WIDTH  = 800, STAGE_HEIGHT = 600;

    static void main() {
        launch();
    }

    private final List<MessageReceiver> mMessageReceivers = new ArrayList<>();
    private Stage mStage;
    private GuiConsole mConsoleStage = new GuiConsole();

    @Override
    public void start(Stage stage) {
        mStage = stage;
        Configuration.init("Vocabulary-Quiz");

        registerReceiver(new Receiver());

        setScene("load");
        mStage.setWidth(STAGE_WIDTH);
        mStage.setHeight(STAGE_HEIGHT);
		mStage.setTitle("Vocabulary Helper");
        try (InputStream inputStream = getClass().getResourceAsStream("/icon/icon.png")) {
            // noinspection ConstantConditions ; NPE will be caught.
            mStage.getIcons().add(new Image(inputStream));
        } catch (IOException | NullPointerException e) {
            JeLib.console().error("Cannot load window icon.");
            JeLib.console().exception(e);
        }
        mStage.show();

        checkForUpdates();
    }

    private void checkForUpdates() {
        final String RELEASES_URL = "https://github.com/iasonasTan/Vocabulary-Quiz/releases/latest";
        final VersionChecker versionChecker = new VersionChecker();
        if(!versionChecker.isUpToDate()) {
            MessageWindow messageWindow = new MessageWindow(
                    "An update is available!",
                    getRootStage(),
                    "An update is available!",
                    "Press the button below to download the latest version."
            );
            messageWindow.addAction("Go to download page", window -> {
                    window.close();
                    getHostServices().showDocument(RELEASES_URL);
            });
            messageWindow.addAction("Not now", Disposable::close);
            Utils.showThemed(messageWindow);
        }
    }

    //Preserves the current theme when switching between scenes
    //Keeps the dark theme checkbox visually in sync with the active stylesheet
    private void setScene(String name) {
        // Load theme from check box
        final Bundle bundle = Configuration.loadBundle(SETTING_THEME_PATH);
        final boolean darkTheme = bundle.getBoolean(DARK_THEME, false);

        final URL url = getClass().getResource(darkTheme ? "/css/dark_theme_style.css" : "/css/light_theme_style.css");
        JeLib.console().log("Showing new scene. Style URL: " + url);

        Parent scene = Context.loadFXML(
                this,
                getClass().getResource("/layout/"+name+".fxml"),
                Objects.requireNonNull(url)
        );

        Scene newScene = new Scene(scene, STAGE_WIDTH, STAGE_HEIGHT);
        newScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        mStage.setScene(newScene);

        // Set theme check box selected if it's found and valid
        if(darkTheme) {
            CheckBox themeCheckbox = (CheckBox)newScene.lookup("#darkThemeCheckbox");
            if(themeCheckbox != null) {
                themeCheckbox.setSelected(true);
            }
        }
    }

    @Override
    public void broadcastMessage(Message data) {
        // noinspection all: Iterating the manual way to prevent CME (ConcurrentModificationException).
        for(int i=0; i<mMessageReceivers.size(); i++) {
            mMessageReceivers.get(i).onReceive(data);
        }
    }

    @Override
    public void registerReceiver(MessageReceiver receiver) {
        mMessageReceivers.add(receiver);
    }

    @Override
    public Stage getRootStage() {
        return mStage;
    }

    private final class Receiver implements MessageReceiver {
        private boolean mEnableConsole = false;

        public Receiver() {
            // noinspection all : mEnableConsole is allways false; value can be changed.
            JeLib.console().setEnabled(mEnableConsole,
                    Console.Type.ERROR, Console.Type.WARNING,
                    Console.Type.INFO, Console.Type.EXCEPTION);
        }

        @Override
        public void onReceive(Message message) {
            if(message.getAction().equals("start_quiz")) {
                startQuiz(message.getBundle().getString("vocabulary"),
                        message.getBundle().getString("savedState"));
            } else if (message.getAction().equals("abort_app")) {
                mStage.close();
                System.exit(0);
            } else if (message.getAction().equals("main")) {
                setScene("load");
            } else if (message.getAction().equals("switch_logs")) {
                mEnableConsole = !mEnableConsole;
                JeLib.console().setEnabled(mEnableConsole,
                        Console.Type.ERROR, Console.Type.WARNING,
                        Console.Type.INFO, Console.Type.EXCEPTION);
                if(mEnableConsole) {
                    mConsoleStage.showWindow();
                } else {
                    mConsoleStage.hideWindow();
                }
            }
        }

        private void startQuiz(String vocab, String savedState) {
            // Show quiz field
            setScene("quiz");

            // Send vocabulary to main
            Message message1 = Message.newBuilder()
                .setAction("initialize_vocabulary")
                .putExtra("vocabulary", vocab)
                .build();

            if(savedState != null) {
                message1.putExtra("savedState", savedState);
            }
            broadcastMessage(message1);
        }
    }
}
