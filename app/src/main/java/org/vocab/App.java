package org.vocab;

import javafx.stage.Stage;
import javafx.scene.*;
import javafx.application.Application;

import com.fxcontext.message.Message;
import com.fxcontext.receiver.MessageReceiver;
import com.fxcontext.main.Context;

import java.util.List;
import java.util.ArrayList;

public class App extends Application implements Context {
    public static final int STAGE_WIDTH  = 800;
    public static final int STAGE_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    private List<MessageReceiver> mMessageReceivers = new ArrayList<>();
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
    }

    private void setScene(String name) {
        Parent scene = Context.loadFXML(
            this,
            getClass().getResource("/layout/"+name+".fxml"),
            getClass().getResource("/style/style.css")
        );
        mStage.setScene(new Scene(scene, STAGE_WIDTH, STAGE_HEIGHT));
    }

    @Override
    public void broadcastMessage(Message data) {
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
                // Show quiz field
                setScene("quiz");

                // Send vocabulary to main
                Message message1 = Message.newBuilder()
                    .setAction("initialize_vocabulary")
                    .putExtra("vocabulary", message.getBundle().getString("vocabulary"))
                    .build();
                broadcastMessage(message1);
            } else if (message.getAction().equals("abort_app")) {
                mStage.close();
                System.exit(0);
            }
        }
    }
}
