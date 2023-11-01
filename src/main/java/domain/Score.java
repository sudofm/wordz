package domain;

import java.util.ArrayList;
import java.util.List;

public class Score {
    private final String correct;
    private final List<Letter> results = new ArrayList<>();
    private int position;

    public Score(String correct) {
        this.correct = correct;
    }
    public Letter letter(int position) {
        return results.get(position);
    }

    public void assess(String attempt) {
        for (char current: attempt.toCharArray()) {
            if (isCorrectLetter(current)) {
                results.add(Letter.CORRECT);
            } else if (occursInWord(current)) {
                results.add(Letter.PART_CORRECT);
            } else {
                results.add(Letter.INCORRECT);
            }
            position++;
        }
    }


    private boolean occursInWord(char current) {
        return correct.contains(String.valueOf(current));
    }

    private boolean isCorrectLetter(char currentLetter) {
        return correct.charAt(position) == currentLetter;
    }
}
