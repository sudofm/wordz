package domain;

public interface GameRepository {
    void create(Game game);

    Game fetchForPlayer(Player eq);

    void update(Game capture);
}
