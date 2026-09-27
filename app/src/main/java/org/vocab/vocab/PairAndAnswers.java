package org.vocab.vocab;

import com.je.core.util.Bundle;
import com.je.io.configuration.Configuration;

/**
 * Holds question and answers.
 * Supplies methods to check the reply.
 */
public record PairAndAnswers(Vocabulary.Pair pair, Vocabulary.Pair[] answers) {
    public static final AnswersCountManager sAnswersCountManager = new AnswersCountManager();

    /**
     * Checks if given data are non-null and valid.
     */
    public PairAndAnswers {
        if (answers == null || pair == null)
            throw new NullPointerException("PairAndAnswers.constructor: 'pair' and 'answers' can't be null.");
        if (answers.length != sAnswersCountManager.getAnswersCount())
            throw new RuntimeException("Length of array 'answers' must be "+sAnswersCountManager.getAnswersCount());
    }

    public boolean isAnswer(int idx) {
        return pair.checkAnswer(answers[idx].getAnswer());
    }

    public String getAnswer(int idx) {
        return answers[idx].getAnswer();
    }

    public String getQuestion() {
        return pair.getQuestion();
    }

    public static final class AnswersCountManager {
        public static final String CONFIGURATION_FILE = "pair_settings.dat";
        public static final String ANSWERS_COUNT = "answers_count";
        private static final int DEFAULT_ANSWERS_COUNT = 3;

        private AnswersCountManager() {
        }

        private int mAnswersCount = DEFAULT_ANSWERS_COUNT;

        public void loadAnswersCount() {
            mAnswersCount = Configuration
                    .loadBundle(CONFIGURATION_FILE)
                    .getInteger(ANSWERS_COUNT, DEFAULT_ANSWERS_COUNT);
        }

        public void setAnswersCount(int count) {
            mAnswersCount = count;
            Bundle bundle = Bundle.builder()
                    .put(ANSWERS_COUNT, count)
                    .build();
            Configuration
                    .storeBundle(CONFIGURATION_FILE, bundle);
        }

        public int getAnswersCount() {
            return mAnswersCount;
        }
    }
}