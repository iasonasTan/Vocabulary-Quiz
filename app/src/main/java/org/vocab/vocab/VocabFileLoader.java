package org.vocab.vocab;

import com.je.core.JeLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class VocabFileLoader {
    private final Vocabulary vocabulary;

    public VocabFileLoader(Vocabulary vocab) {
        vocabulary = vocab;
    }

    public void loadVocab(InputStreamReader reader) throws IOException {
        JeLib.console().log("Loading pairs...");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while((line = bufferedReader.readLine()) != null) {
            JeLib.console().log("Loading pair: " + line);
            vocabulary.addUnconfigured(line);
        }
    }
}