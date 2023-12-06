package adapters.api;

import com.google.gson.Gson;
import com.vtence.molecule.Request;
import com.vtence.molecule.Response;
import com.vtence.molecule.WebServer;
import com.vtence.molecule.http.HttpStatus;
import com.vtence.molecule.routing.Routes;
import domain.Player;
import domain.Wordz;

import java.io.IOException;

public class WordzEndpoint {

    private final Wordz wordz;
    private final WebServer server;
    public WordzEndpoint(Wordz wordz, String host, int port) {
        server = WebServer.create(host, port);
        this.wordz = wordz;
        try {
            server.route(new Routes() {{
                post("/start")
                        .to(request -> startGame(request));
            }});
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }

    private Response startGame(Request request) {

        try {
            Player player =
                    new Gson().fromJson(request.body(), Player.class);

            boolean isSuccessful = wordz.newGame(player);
            if (isSuccessful) {
                return Response
                        .of(HttpStatus.NO_CONTENT)
                        .done();
            }
            return Response.of(HttpStatus.CONFLICT).done();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
