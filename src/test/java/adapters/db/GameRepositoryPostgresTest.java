package adapters.db;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import domain.Game;
import domain.GameRepository;
import domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseSensitiveTableNames = true, caseInsensitiveStrategy = Orthography.LOWERCASE)
class GameRepositoryPostgresTest {

    private DataSource dataSource;

    private final ConnectionHolder connectionHolder = () -> dataSource.getConnection();

    @BeforeEach
    void setUp() {
        this.dataSource = new PostgresTestDataSource();
    }

    @Test
    @DataSet(value = "adapters/data/emptyGame.json", cleanBefore = true)
    @ExpectedDataSet(value = "/adapters/data/createGame.json")
    void storesNewGame() {
        var player = new Player("player1");
        var game = new Game(player, "BONUS", 0, false);

        GameRepository games = new GameRepositoryPostgres(dataSource);
        games.create(game);
    }

    @Test
    void fetchesGame() {
        GameRepository games = new GameRepositoryPostgres(dataSource);

        var player = new Player("player1");
        Optional<Game> game = games.fetchForPlayer(player);

        assertThat(game.isPresent()).isTrue();
        var actual = game.get();
        assertThat(actual.getPlayer()).isEqualTo(player);
        assertThat(actual.getWord()).isEqualTo("BONUS");
        assertThat(actual.getAttemptNumber()).isZero();
        assertThat(actual.isGameOver()).isFalse();
    }

    @Test
    void update() {
    }
}