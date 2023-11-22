package adapters.api;

import com.google.gson.Gson;
import com.vtence.molecule.http.HttpStatus;
import domain.Player;
import domain.Wordz;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WordzEndpointTest {

    @Mock
    private Wordz wordz;

    private static final Player PLAYER = new Player("Maxime");
    @Test
    void startGame() throws IOException, InterruptedException {
        var endpoint = new WordzEndpoint(wordz, "localhost", 8080);

        when(wordz.newGame(eq(PLAYER)))
                .thenReturn(true);
        var req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/start"))
                .POST(HttpRequest.BodyPublishers
                        .ofString(new Gson().toJson(PLAYER)))
                .build();

        var httpClient = HttpClient.newHttpClient();
        HttpResponse res = httpClient.send(req, HttpResponse.BodyHandlers.discarding());

       assertThat(res).hasStatusCode(HttpStatus.NO_CONTENT.code);
    }
}
