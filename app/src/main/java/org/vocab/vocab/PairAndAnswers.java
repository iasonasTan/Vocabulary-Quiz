package org.vocab.vocab;

/**
 * Holds question and answers.
 * Supplies methods to check the reply.
 */
public record PairAndAnswers(Vocabulary.Pair pair, Vocabulary.Pair[] answers) {
    /**
     * Checks if given data are non-null and valid.
     */
    public PairAndAnswers {
        if (answers == null) throw new NullPointerException("No value can be null.");
        if (answers.length != 3) throw new RuntimeException("Length of array 'answers' must be 3.");
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
}
