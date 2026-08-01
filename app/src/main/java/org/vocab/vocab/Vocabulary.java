package org.vocab.vocab;

import com.je.core.JeLib;

import java.util.*;

public final class Vocabulary {
    private static final int MINIMUM_WORDS = 3;

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

    public Vocabulary(String data) throws ArrayIndexOutOfBoundsException {
        loadString(data);
    }

    public void add(String unsplit) throws ArrayIndexOutOfBoundsException, NumberFormatException {
        String[] split = unsplit.strip().split("[=:]");
        add(split[0], split[1], Integer.parseInt(split[2]));
    }

    public void add(String key, String value) {
        mPairs.add(new Pair(key.strip(), value.strip()));
    }

    public void add(String key, String value, int learned) {
        mPairs.add(new Pair(key.strip(), value.strip(), learned));
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
        Pair distractor1 = randomPair(), distractor2 = randomPair();

        List<Pair> randomValues = new ArrayList<>(List.of(pair, distractor1, distractor2)); // Put values in a shuffleable list.
        Collections.shuffle(randomValues); // Shuffle answers.

        // Return values packed in a PairAndAnswers record.
        return Optional.of(new PairAndAnswers(pair, randomValues.toArray(new Pair[0])));
    }

    public void setReverse(boolean reverse) {
        JeLib.console().log("Reverse: "+reverse);
        Pair.sReverse = reverse;
    }

    public boolean isReverse() {
        return Pair.sReverse;
    }

    public void loadString(String data) throws ArrayIndexOutOfBoundsException {
        String[] pairs = data.split(","); // Get pairs
        for(String unsplitPair: pairs) {
            if(!unsplitPair.contains("=") || !unsplitPair.contains(":")) {
                continue;
            }
            JeLib.console().log("Adding pair: "+unsplitPair);
            add(unsplitPair);
        }
    }

    /**
     * Holds a pair of words, and it's learned value.
     */
    protected final class Pair implements Comparable<Pair> {
        /**
         * A key and it's value.
         */
        private final String key, value;

        /**
         * If true, key and value will be reversed.
         */
        public static boolean sReverse = false;

        /**
         * Indicates if the word is either learned or not learned.
         * The higher this it, the better the user knows the value,
         * the lower this is, the less the user knows the value.
         */
        private int learned;

        public Pair(String key, String value, int learned) {
            this.key = key;
            this.value = value;
            this.learned = learned;
        }

        public Pair(String key, String value) {
            this(key, value, 0);
        }

        /**
         * Checks if given answer is the correct.
         * If the answer is the correct one, field 'learned' gets increased by one and Vocabulary.this.mPairs gets re-sorted;
         * otherwise, field 'learned' gets decreased by two and list Vocabulary.this.mPairs doesn't get re-sorted.
         * @param answer answer to check.
         * @return Returns true if the given answer is the correct one; false otherwise.
         */
        public boolean checkAnswer(String answer) {
            if(answer.equals(getAnswer()) || answer.equals(getQuestion())) {
                learned++;
                Collections.sort(mPairs);
                return true;
            }
            learned-=2;
            return false;
        }

        public String getQuestion() {
            return !sReverse?key:value;
        }

        public String getAnswer() {
            return !sReverse?value:key;
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
            return key+"="+value+":"+learned;
        }
    }

    public boolean hasMinimumWords() {
        return mPairs.size() >= MINIMUM_WORDS;
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