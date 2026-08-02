package org.vocab.vocab;

import com.je.core.JeLib;

import java.io.*;

public final class AppStateIO {
    /**
     * Prevent instantiation.
     */
    private AppStateIO(){}

    /**
     * Writes the vocabulary app state to a file.
     * Uses {@link Vocabulary#toString()} transform.
     * @param outputStream Where to store the vocabulary.
     */
    public static void write(OutputStream outputStream, Vocabulary vocabulary) throws IOException {
        try(OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            BufferedWriter buffWriter = new BufferedWriter(writer)) {
            for(Object line: vocabulary.toString().lines().toArray()) {
                buffWriter.write(line.toString());
                buffWriter.newLine();
            }
        }
    }

    /**
     * Loads the vocabulary app state from a file.
     * Uses {@link Vocabulary#toString()} transform.
     * @param inputStream Where to load the vocabulary from.
     */
    public static void load(InputStream inputStream, Vocabulary vocabulary) throws IOException {
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
        }
    }
}
