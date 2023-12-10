package adapters.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vtence.molecule.Request;
import com.vtence.molecule.Response;
import com.vtence.molecule.WebServer;
import com.vtence.molecule.http.HttpStatus;
import com.vtence.molecule.routing.Routes;
import domain.GuessResult;
import domain.Player;
import domain.Wordz;

import java.io.IOException;

import static com.vtence.molecule.http.HttpStatus.BAD_REQUEST;
import static com.vtence.molecule.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class WordzEndpoint {

    private final Wordz wordz;

    public WordzEndpoint(Wordz wordz, String host, int port) {
        var server = WebServer.create(host, port);
        this.wordz = wordz;

        try {
            server.route(new Routes() {{
                post("/start").to(request -> startGame(request));
                post("/guess").to(request -> guessWord(request));
            }});
        } catch (IOException e) {
            throw new IllegalStateException("Wordz HTTP endpoint could not start", e);
        }
    }

    private Response startGame(Request request) {
        try {
            Player player = extractPlayer(request);

            boolean isSuccessful = wordz.newGame(player);
            HttpStatus status = isSuccessful ? HttpStatus.NO_CONTENT : HttpStatus.CONFLICT;
            return Response.of(status).done();
        } catch (IOException | JsonSyntaxException e) {
            return Response.of(BAD_REQUEST).done();
        }
    }

    private Response guessWord(Request request) {
        try {
            GuessRequest gr = extractGuessRequest(request);
            GuessResult result = wordz.assess(gr.player(), gr.guess());

            if (result.isError()) {
                return Response.of(INTERNAL_SERVER_ERROR).done();
            }

            return Response.ok()
                    .body(createGuessHttpResponse(result))
                    .done();
        } catch (IOException e) {
            return Response.of(INTERNAL_SERVER_ERROR).done();
        }
    }

    private GuessRequest extractGuessRequest(Request request) throws IOException {
        return new Gson().fromJson(request.body(), GuessRequest.class);
    }

    private String createGuessHttpResponse(GuessResult result) {
        GuessHttpResponse httpResponse
                = new GuessHttpResponseMapper().from(result);
        return new Gson().toJson(httpResponse);
    }


    private Player extractPlayer(Request request) throws IOException {
        return new Gson().fromJson(request.body(), Player.class);
    }
}
