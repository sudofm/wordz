package adapters.api;

import com.google.gson.Gson;
import com.vtence.molecule.http.HttpStatus;
import domain.GuessResult;
import domain.Player;
import domain.Score;
import domain.Wordz;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.vtence.molecule.testing.http.HttpResponseAssert.assertThat;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WordzEndpointTest {

    private Wordz mockWordz;
    private WordzEndpoint endpoint;
    private static final Player PLAYER = new Player("Maxime");
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeAll
    void setUp() {
        mockWordz = mock(Wordz.class);
        endpoint = new WordzEndpoint(mockWordz, "localhost", 8080);
    }

    @Test
    void startGame() throws IOException, InterruptedException {
        when(mockWordz.newGame(eq(PLAYER)))
                .thenReturn(true);

        var req = requestBuilder("start")
                .POST(asJsonBody(PLAYER))
                .build();

        var httpClient = HttpClient.newHttpClient();

        var res = httpClient.send(req, HttpResponse.BodyHandlers.discarding());

       assertThat(res).hasStatusCode(HttpStatus.NO_CONTENT.code);
    }

    @Test
    void rejectsRestart() throws Exception {
        when(mockWordz.newGame(eq(PLAYER))).thenReturn(false);

        var req = requestBuilder("start").POST(asJsonBody(PLAYER)).build();

        var res = httpClient.send(req, HttpResponse.BodyHandlers.discarding());

        assertThat(res).hasStatusCode(HttpStatus.CONFLICT.code);
    }

    @Test
    void partiallyCorrectGuess() throws Exception {
        // Score: part correct, correct, incorrect, incorrect, incorrect
        var score = new Score("-U--G");
        score.assess("GUESS");

        var result = new GuessResult(score, false, false);
        when(mockWordz.assess(eq(PLAYER), eq("GUESS")))
                .thenReturn(result);

        var guessRequest = new GuessRequest(PLAYER, "GUESS");
        var body = new Gson().toJson(guessRequest);
        var req = requestBuilder("guess")
                .POST(ofString(body))
                .build();

        var res
                = httpClient.send(req,
                HttpResponse.BodyHandlers.ofString());

        var response
                = new Gson().fromJson(res.body(), GuessHttpResponse.class);

        // Key to letters in scores():
        // C correct, P part correct, X incorrect
        Assertions.assertThat(response.scores()).isEqualTo("PCXXX");
        Assertions.assertThat(response.isGameOver()).isFalse();
    }

    @Test
    void rejectsMalformedRequest() throws Exception {
        var req = requestBuilder("start")
                .POST(ofString("malformed"))
                .build();

        var res
                = httpClient.send(req,
                HttpResponse.BodyHandlers.discarding());

        assertThat(res).hasStatusCode(HttpStatus.BAD_REQUEST.code);
    }

    @Test
    void reportsError() throws Exception {
        var result = new GuessResult(new Score("-----"),
                false, true);
        when(mockWordz.assess(eq(PLAYER), eq("GUESS")))
                .thenReturn(result);

        var guessRequest = new GuessRequest(PLAYER, "GUESS");
        var body = new Gson().toJson(guessRequest);
        var req = requestBuilder("guess")
                .POST(ofString(body))
                .build();

        var res
                = httpClient.send(req,
                HttpResponse.BodyHandlers.ofString());

        assertThat(res).hasStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.code);
    }

    private HttpRequest.Builder requestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/" + path));
    }

    private HttpRequest.BodyPublisher asJsonBody(Object source) {
        return ofString(new Gson().toJson(source));
    }
}
