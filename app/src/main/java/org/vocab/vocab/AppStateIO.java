package org.vocab.vocab;

import com.je.core.JeLib;
import com.jjfx.context.Context;
import com.jjfx.utils.MessageWindow;
import org.vocab.util.MWUtils;

import java.io.*;

public final class AppStateIO {
    /**
     * Prevent instantiation.
     */
    private AppStateIO(){}

    /**
     * Writes the vocabulary to a file.
     * Uses {@link Vocabulary#toString()} transform.
     * @param context      App context used for gui.
     * @param outputStream Where to store the vocabulary.
     */
    public static void write(Context context, OutputStream outputStream, Vocabulary vocabulary) {
        try(OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            BufferedWriter buffWriter = new BufferedWriter(writer)) {
            for(Object line: vocabulary.toString().lines().toArray()) {
                buffWriter.write(line.toString());
                buffWriter.newLine();
            }
        } catch (IOException ioe) {
            JeLib.console().error("Could not write vocabulary. " + ioe);
            MessageWindow messageWindow = new MessageWindow(
                    "Vocabulary Quiz - Error",
                    context.getRootStage(),
                    "Cannot write state.",
                    "An error occurred while trying to save this state. Please try again."
            );
            messageWindow.addAction("Ok", MessageWindow::close);
            MWUtils.showThemed(messageWindow);
        }
    }

    /**
     * Loads the vocabulary from a file.
     * Uses {@link Vocabulary#toString()} transform.
     * @param context      App context used for gui.
     * @param inputStream Where to load the vocabulary from.
     */
    public static void load(Context context, InputStream inputStream, Vocabulary vocabulary) {
        try(InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader)) {

            JeLib.console().log("Loading state from input stream: " + inputStream);
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                builder.append(line)
                        .append('\n');
            }
            builder.setLength(Math.max(0, builder.length()-1));

            JeLib.console().log("Sending string to vocabulary instance: "+ builder);
            vocabulary.loadString(builder.toString());
        } catch (IOException ioe) {
            JeLib.console().error("Could not load vocabulary. " + ioe);
            MessageWindow messageWindow = new MessageWindow(
                    "Vocabulary Quiz - Error",
                    context.getRootStage(),
                    "Cannot load state.",
                    "An error occurred while trying to load this state. Please try again."
            );
            messageWindow.addAction("Ok", MessageWindow::close);
            MWUtils.showThemed(messageWindow);
        }
    }
}
