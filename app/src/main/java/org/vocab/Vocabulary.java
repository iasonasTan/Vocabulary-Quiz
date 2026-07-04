package org.vocab;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public final class Vocabulary {
    private Map<String, String> mData = new HashMap<>();

    public Vocabulary() {
    }

    public Vocabulary(String data) {
        String[] pairs = data.split(",");
        for(String pair: pairs) {
            String[] pairSplit = pair.split("=");
            mData.put(pairSplit[0], pairSplit[1]);
        }
    }

    public void add(String unsplit) throws ArrayIndexOutOfBoundsException {
        String[] split = unsplit.replace(" ", "").split("=");
        add(split[0], split[1]);
    }

    public void add(String word, String translation) {
        mData.put(word, translation);
    }

    /**
     * Returns a DataPair that contains a question,
     * the correct answers, 
     * and a list with three answers (one of them is right).
     */
    public DataPair getRandom() {
        // Get a random key
        Set<String> keysSet = mData.keySet();
        String[] keys = (String[])keysSet.toArray(new String[keysSet.size()]);
        String key = keys[(int)(Math.random() * keys.length)];

        Collection<String> valuesList = mData.values();

        String[] values = (String[])valuesList.toArray(new String[valuesList.size()]);

        // Get one correct value
        String value1 = mData.get(key);

        // Get two random values
        String value2 = values[(int)(Math.random() * values.length)];
        String value3 = values[(int)(Math.random() * values.length)];

        // Put values in a list
        List<String> randomValues = new ArrayList<String>(List.of(value1, value2, value3));

        // Shuffle the list
        Collections.shuffle(randomValues);

        // Return values packed in a DataPair record.
        return new DataPair(key, (String[])randomValues.toArray(new String[randomValues.size()]), value1);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        mData.forEach((key, value) -> {
            builder.append(key+"="+value)
                .append(',');
        });
        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    public record DataPair(String question, String[] answers, String correctAnswer) {
        public String getAnswer0() {
            return answers[0];
        }

        public String getAnswer1() {
            return answers[1];
        }

        public String getAnswer2() {
            return answers[2];
        }

        public boolean isAnswer0() {
            return getAnswer0().equals(correctAnswer);
        }

        public boolean isAnswer1() {
            return getAnswer1().equals(correctAnswer);
        }

        public boolean isAnswer2() {
            return getAnswer2().equals(correctAnswer);
        }
    }
}