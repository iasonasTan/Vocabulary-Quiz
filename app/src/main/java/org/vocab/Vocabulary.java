package org.vocab;

import java.util.*;

public final class Vocabulary {
    private final List<Pair> mPairs = new ArrayList<>() {
        /**
         * Override for debugging.
         */
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            // Append a pair and then a comma
            mPairs.forEach(pair -> builder
                    .append(pair.toString())
                    .append(" (Learned: ")
                    .append(pair.getLearned())
                    .append(")")
                    .append('\n'));
            // Remove the last comma
            builder.setLength(Math.max(builder.length() - 1, 0));
            return builder.toString();
        }
    };

    public Vocabulary() {
    }

    public Vocabulary(String data) {
        String[] pairs = data.split(","); // Get pairs
        for(String unsplitPair: pairs) {
            if(!unsplitPair.contains("=")) {
                continue;
            }
            add(unsplitPair);
        }
    }

    public void add(String unsplit) throws ArrayIndexOutOfBoundsException {
        String[] split = unsplit.replace(" ", "").split("=");
        add(split[0], split[1]);
    }

    public void add(String key, String value) {
        mPairs.add(new Pair(key, value));
    }

    /**
     * Returns a random pair from the list.
     * The method selects a random pair from the first half of the list,
     * where there are the less `learned` pairs.
     * This way, the user will mostly receive the less `learned` pairs instead of the ones he knows.
     * @return Returns a random pair from the first half of the list.
     */
    private Pair randomPair() {
        return mPairs.get((int)(Math.random() * mPairs.size() /2));
    }

    /**
     * Returns a PairAndAnswers that contains a question,
     * the correct answers, 
     * and a list with three answers (one of them is right).
     */
    public Optional<PairAndAnswers> randomPairAndAnswers() {
        // Returns an empty optional if there's no data.
        if(mPairs.isEmpty()) {
            return Optional.empty();
        }

        Pair pair = randomPair();
        String value2 = randomPair().getAnswer();
        String value3 = randomPair().getAnswer();

        List<String> randomValues = new ArrayList<>(List.of(pair.getAnswer(), value2, value3)); // Put values in a shuffleable list.
        Collections.shuffle(randomValues); // Shuffle answers.

        // Return values packed in a PairAndAnswers record.
        return Optional.of(new PairAndAnswers(pair, randomValues.toArray(new String[0])));
    }

    /**
     * Holds a pair of words, and it's learned value.
     */
    private final class Pair implements Comparable<Pair> {
        /**
         * A key and it's value.
         */
        private final String key, value;

        /**
         * Indicates if the word is either learned or not learned.
         * The higher this it, the better the user knows the value,
         * the lower this is, the less the user knows the value.
         */
        private int learned = 0;

        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Checks if given answer is the correct.
         * If the answer is the correct one, field 'learned' gets increased by one and Vocabulary.this.mPairs gets re-sorted;
         * otherwise, field 'learned' gets decreased by two and list Vocabulary.this.mPairs doesn't get re-sorted.
         * @param answer answer to check.
         * @return Returns true if the given answer is the correct one; false otherwise.
         */
        public boolean checkAnswer(String answer) {
            if(answer.equals(value)) {
                learned++;
                Collections.sort(mPairs);
                return true;
            }
            learned-=2;
            return false;
        }

        public String getQuestion() {
            return key;
        }

        public String getAnswer() {
            return value;
        }

        public int getLearned() {
            return learned;
        }

        @Override
        public int compareTo(Pair o) {
            return Integer.compare(learned, o.learned);
        }

        @Override
        public String toString() {
            return key+"="+value;
        }
    }

    /**
     * Holds question and answers.
     * Supplies methods to check the reply.
     */
    public record PairAndAnswers(Pair pair, String[] answers) {
        /**
         * Checks if given data are non-null and valid.
         */
        public PairAndAnswers {
            if(answers == null)     throw new NullPointerException("No value can be null.");
            if(answers.length != 3) throw new RuntimeException("Length of array 'answers' must be 3.");
        }

        public boolean isAnswer0() {
            return pair.checkAnswer(answers[0]);
        }

        public boolean isAnswer1() {
            return pair.checkAnswer(answers[1]);
        }

        public boolean isAnswer2() {
            return pair.checkAnswer(answers[2]);
        }

        public String getQuestion() {
            return pair.getQuestion();
        }
    }

    /**
     * Returns containing data (pairs) in the below format:<br>
     * {@code Format: "key1=value1,key2=value2,key3=value3..."}<br>
     * If the object is empty, empty strings is returned.
     * @return Returns containing data in the above format, an empty string if there's no data (pairs).
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        // Append a pair and then a comma
        mPairs.forEach(pair -> builder.append(pair.toString()).append(','));
        // Remove the last comma
        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }
}