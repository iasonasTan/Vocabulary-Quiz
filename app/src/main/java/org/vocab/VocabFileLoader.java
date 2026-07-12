package org.vocab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class VocabFileLoader {
    private final Vocabulary vocabulary;

    public VocabFileLoader(Vocabulary vocab) {
        vocabulary = vocab;
    }

    public void loadVocab(InputStreamReader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while((line = bufferedReader.readLine()) != null) {
            String[] lineSplit = line.split("=");
            vocabulary.add(lineSplit[0], lineSplit[1]);
            IO.println("Pair: " + lineSplit[0] + " === " + lineSplit[1]);
        }
    }
}