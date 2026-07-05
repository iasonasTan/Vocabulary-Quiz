package org.vocab;

import org.vocab.Vocabulary;

import java.util.Properties;

import java.io.IOException;
import java.io.InputStreamReader;

public final class VocabFileLoader {
    private Vocabulary vocabulary;

    public VocabFileLoader(Vocabulary vocab) {
        vocabulary = vocab;
    }

    public void loadVocab(InputStreamReader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        properties.forEach((k, v) -> {
            vocabulary.add((String)k, (String)v);
        });
    }
}