package adapters.api;

import domain.GuessResult;
import domain.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GuessHttpResponseMapperTest {
    private GuessHttpResponse actual;

    @BeforeEach
    private void setUp() {
        Score score = new Score("ABCZZ");
        score.assess("ACBXX");

        var guessResult = new GuessResult(score, true, true);

        actual = new GuessHttpResponseMapper().from(guessResult);
    }

    @Test
    void mapsCorrectLetter() {
        assertThat(actual.scores().charAt(0)).isEqualTo('C');
    }

    @Test
    void mapsPartCorrectLetter() {
        assertThat(actual.scores().charAt(1)).isEqualTo('P');
    }

    @Test
    void mapsIncorrectLetter() {
        assertThat(actual.scores().charAt(4)).isEqualTo('X');
    }

    @Test
    void mapsGameOver() {
        assertThat(actual.isGameOver()).isTrue();
    }
}