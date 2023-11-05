package domain;

public class Game {

    private static final int MAXIMUM_NUMBER_ALLOWED_GUESSES = 5;
    private Player player;
    private String targetWord;
    private int attemptNumber;
    private boolean isGameOver;
    public Game(Player player, String targetWord, int attemptNumber, boolean isGameOver) {
        this.player = player;
        this.targetWord = targetWord;
        this.attemptNumber = attemptNumber;
        this.isGameOver = isGameOver;

    }

    public Game(Player player, String targetWord, int attemptNumber) {
        this.player = player;
        this.targetWord = targetWord;
        this.attemptNumber = attemptNumber;
    }


    public static Game create(Player player, String correctWord) {
        return new Game(player, correctWord, 0, false);
    }

    public static Game create(Player player, String correctWord, int attemptNumber) {
        return new Game(player, correctWord, attemptNumber, false);
    }

    public String getWord() {
        return targetWord;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public Player getPlayer() {
        return player;
    }

    private void trackNumberOfAttempts() {
        attemptNumber++;

        if (attemptNumber == MAXIMUM_NUMBER_ALLOWED_GUESSES) {
            end();
        }
    }

    public Score attempt(String guess) {
        trackNumberOfAttempts();

        var word = new Word(targetWord);
        Score score = word.guess(guess);

        if (score.allCorrect()) {
            end();
        }

        return score;
    }


    public boolean hasNoRemainingGuesses() {
        return attemptNumber == MAXIMUM_NUMBER_ALLOWED_GUESSES;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void end() {
        this.isGameOver = true;
    }
}
