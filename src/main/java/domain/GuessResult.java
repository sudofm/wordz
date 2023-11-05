package domain;

public record GuessResult(Score score, boolean isGameOver, boolean isError) {
    public static final GuessResult ERROR = new GuessResult(null, true, true);
    static GuessResult create(Score score, boolean isGameOver) {
        return new GuessResult(score, isGameOver, false);
    }
}
