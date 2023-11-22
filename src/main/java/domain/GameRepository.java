package domain;

import java.util.Optional;

public interface GameRepository {
    void create(Game game);

    Optional<Game> fetchForPlayer(Player eq);

    void update(Game capture);
}
