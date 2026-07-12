package org.vocab;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.application.Application;

import com.fxcontext.message.Message;
import com.fxcontext.receiver.MessageReceiver;
import com.fxcontext.main.Context;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class App extends Application implements Context {
    public static final int STAGE_WIDTH  = 800;
    public static final int STAGE_HEIGHT = 600;

    static void main(String[] args) {
        launch(args);
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
		mStage.show();

        checkForUpdates(mStage);
    }

    @SuppressWarnings("all") // TODO: Refactor method in class.
    private void checkForUpdates(final Stage parentStage) {
        final String RELEASES_URL = "http://github.com/iasonasTan/Vocabulary-Quiz/releases/latest";

        final Supplier<List<Node>> nodeSupplier = () -> {
            Label label = new Label("An update is available!");
            label.setStyle("-fx-font-weight: bold; -fx-font-size: large;");

            Label label1 = new Label("Press the button below to download the latest version.");

            Button button = new Button("Go to download page.");
            button.setOnAction(_ -> {
                getHostServices().showDocument(RELEASES_URL);
            });

            return List.of(label, label1, button);
        };

        VersionChecker versionChecker = new VersionChecker();
        final int WIN_WIDTH = 400, WIN_HEIGHT = 200;
        if(!versionChecker.isUpToDate()) {
            Stage stage = new Stage();
            stage.initOwner(parentStage);
            stage.setAlwaysOnTop(true);

            VBox parent = new VBox();
            parent.setAlignment(Pos.CENTER);
            parent.setSpacing(3);
            parent.getChildren().addAll(nodeSupplier.get());

            stage.setScene(new Scene(parent, WIN_WIDTH, WIN_HEIGHT));
            stage.setWidth(WIN_WIDTH);
            stage.setHeight(WIN_HEIGHT);
            stage.setTitle("Update available!");
            stage.sizeToScene();
            stage.show();
        }
    }

    private void setScene(String name) {
        Parent scene = Context.loadFXML(
            this,
            getClass().getResource("/layout/"+name+".fxml"),
                Objects.requireNonNull(getClass().getResource("/style/style.css"))
        );
        mStage.setScene(new Scene(scene, STAGE_WIDTH, STAGE_HEIGHT));
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
