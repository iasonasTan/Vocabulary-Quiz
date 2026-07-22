package org.vocab;

import com.je.core.JeLib;
import com.jjfx.context.Context;
import com.jjfx.message.Message;
import com.jjfx.receiver.MessageReceiver;
import com.jjfx.utils.MessageWindow;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App extends Application implements Context {
    public static final int STAGE_WIDTH  = 800;
    public static final int STAGE_HEIGHT = 600;

    static void main() {
        launch();
    }

    private final List<MessageReceiver> mMessageReceivers = new ArrayList<>();
    private Stage mStage;

    @Override
    public void start(Stage stage) {
        mStage = stage;

        registerReceiver(new Receiver());

        setScene("main");
        mStage.setWidth(STAGE_WIDTH);
        mStage.setHeight(STAGE_HEIGHT);
		mStage.setTitle("Vocabulary Helper");
        try (InputStream inputStream = getClass().getResourceAsStream("/icon/icon.png")) {
            // noinspection ConstantConditions ; NPE will be caught.
            mStage.getIcons().add(new Image(inputStream));
        } catch (IOException | NullPointerException e) {
            JeLib.console().error("Cannot load window icon.");
            e.printStackTrace();
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
                    mStage,
                    "An update is available!",
                    "Press the button below to download the latest version."
            );
            messageWindow.addAction("Go to download page", window -> {
                    window.closeWindow();
                    getHostServices().showDocument(RELEASES_URL);
            });
            messageWindow.addAction("Not now", MessageWindow::closeWindow);
            messageWindow.showWindow();
        }
    }

    //Preserves the current theme when switching between scenes
    //Keeps the dark theme checkbox visually in sync with the active stylesheet
    private void setScene(String name) {
        String currentTheme = null;

        Parent scene = Context.loadFXML(
                this,
                getClass().getResource("/layout/"+name+".fxml"),
                Objects.requireNonNull(getClass().getResource("/style/light_theme_style.css"))
        );

        Scene newScene = new Scene(scene, STAGE_WIDTH, STAGE_HEIGHT);

        if(mStage.getScene() != null) {
            for(String theme : mStage.getScene().getStylesheets()){
                if(theme.contains("dark_theme_style") || theme.contains("light_theme_style")){
                    currentTheme = theme;
                    break;
                }
            }
        }

        if(currentTheme != null) {
            newScene.getStylesheets().add(currentTheme);
        }

        mStage.setScene(newScene);

        CheckBox themeCheckbox = (CheckBox) newScene.lookup("#darkThemeCheckbox");
        if(themeCheckbox != null && currentTheme != null && currentTheme.contains("dark_theme_style")) {
            themeCheckbox.setSelected(true);
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
        @Override
        public void onReceive(Message message) {
            if(message.getAction().equals("start_quiz")) {
                startQuiz(message.getBundle().getString("vocabulary"));
            } else if (message.getAction().equals("abort_app")) {
                mStage.close();
                System.exit(0);
            } else if (message.getAction().equals("main")) {
                setScene("main");
            }
        }

        private void startQuiz(String vocab) {
            // Show quiz field
            setScene("quiz");

            // Send vocabulary to main
            Message message1 = Message.newBuilder()
                .setAction("initialize_vocabulary")
                .putExtra("vocabulary", vocab)
                .build();
            broadcastMessage(message1);
        }
    }
}
