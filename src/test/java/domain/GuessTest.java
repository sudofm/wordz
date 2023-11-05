package domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GuessTest {
    private static final Player PLAYER = new Player();
    private static final String CORRECT_WORD = "ARISE";
    private static final String WRONG_WORD = "RXXXX";

    @Mock
    private GameRepository gameRepository;
    @Mock
    private WordRepository wordRepository;
    @Mock
    private RandomNumbers randomNumbers;
    @InjectMocks
    private Wordz wordz;

    @Test
    void returnsScoreForGuess() {
        givenGameInRepository(Game.create(PLAYER, CORRECT_WORD));

        GuessResult result = wordz.assess(PLAYER, WRONG_WORD);

        Letter firstLetter = result.score().letter(0);
        assertThat(firstLetter).isEqualTo(Letter.PART_CORRECT);
    }

    @Test
    void updatesAttemptNumber() {
        givenGameInRepository(Game.create(PLAYER, CORRECT_WORD));

        wordz.assess(PLAYER, WRONG_WORD);

        var game = getUpdateGameInRepository();
        assertThat(game.getAttemptNumber()).isEqualTo(1);
    }

    @Test
    void reportsGameOverOnCorrectGuess() {
        var player = new Player();
        Game game = new Game(player, "ARISE", 0);
        when(gameRepository.fetchForPlayer(player)).thenReturn(game);
        var wordz = new Wordz(gameRepository, wordRepository, randomNumbers);

        var guess = "ARISE";
        GuessResult result = wordz.assess(player, guess);

        assertThat(result.isGameOver()).isTrue();
    }

    @Test
    void gameOverOnTooManyIncorrectGuesses() {
        int maximumGuesses = 5;
        givenGameInRepository(Game.create(PLAYER, CORRECT_WORD, maximumGuesses-1));

        GuessResult result = wordz.assess(PLAYER, WRONG_WORD);
        assertThat(result.isGameOver()).isTrue();
    }

    @Test
    void rejectsGuessAfterGameOver() {
        var game= Game.create(PLAYER, CORRECT_WORD);
        game.end();
        givenGameInRepository(game);

        GuessResult result = wordz.assess(PLAYER, CORRECT_WORD);

        assertThat(result.isError()).isTrue();
    }

    @Test
    void recordsGameOverOnCorrectGuess() {
        givenGameInRepository(Game.create(PLAYER, CORRECT_WORD));

        wordz.assess(PLAYER, CORRECT_WORD);

        Game game = getUpdateGameInRepository();
        assertThat(game.isGameOver()).isTrue();
    }


    private Game getUpdateGameInRepository() {
        ArgumentCaptor<Game> argument = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).update(argument.capture());
        return argument.getValue();
    }

    private void givenGameInRepository(Game game) {
        when(gameRepository.fetchForPlayer(eq(PLAYER)))
                .thenReturn(game);
    }


}
