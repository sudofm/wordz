package adapters.api;

import com.google.gson.Gson;
import com.vtence.molecule.http.HttpStatus;
import domain.Player;
import domain.Wordz;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.vtence.molecule.testing.http.HttpResponseAssert.assertThat;
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
        HttpResponse res = httpClient.send(req, HttpResponse.BodyHandlers.discarding());

       assertThat(res).hasStatusCode(HttpStatus.NO_CONTENT.code);
    }

    @Test
    void rejectsRestart() throws Exception {
        when(mockWordz.newGame(eq(PLAYER))).thenReturn(false);

        var req = requestBuilder("start").POST(asJsonBody(PLAYER)).build();

        var res = httpClient.send(req, HttpResponse.BodyHandlers.discarding());

        assertThat(res).hasStatusCode(HttpStatus.CONFLICT.code);
    }

    private HttpRequest.Builder requestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/" + path));
    }

    private HttpRequest.BodyPublisher asJsonBody(Object source) {
        return (HttpRequest.BodyPublisher) HttpRequest.BodyPublishers
                .ofString(new Gson().toJson(source));
    }
}
