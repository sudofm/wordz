package domain;

import java.util.Optional;

public class Wordz {

    private final GameRepository gameRepository;
    private final WordSelection wordSelection;

    public Wordz(GameRepository gr, WordRepository wr, RandomNumbers rn) {
        this.gameRepository = gr;
        this.wordSelection = new WordSelection(wr, rn);
    }
    public boolean newGame(Player player) {
        Optional<Game> currentGame = gameRepository.fetchForPlayer(player);

        if (isGameInProgress(currentGame)) {
            return false ;
        }
        var word = wordSelection.chooseRandomWord();
        Game game = new Game(player, word, 0, false);
        gameRepository.create(Game.create(player, word));
        return true;
    }

    public GuessResult assess(Player player, String guess) {
        Optional<Game> currentGame = gameRepository.fetchForPlayer(player);
        
        if (!isGameInProgress(currentGame)) {
            return GuessResult.ERROR;
        }
        return calculateScore(currentGame.get(), guess);
    }

    private GuessResult calculateScore(Game game, String guess) {
        Score score = game.attempt( guess );

        gameRepository.update(game);
        return GuessResult.create(score, game.isGameOver());
    }

    private boolean isGameInProgress(Optional<Game> currentGame) {
        if (currentGame.isEmpty()) {
            return false ;
        }
        return !currentGame.get().isGameOver();
    }


}
