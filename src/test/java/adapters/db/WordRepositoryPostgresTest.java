package adapters.db;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import domain.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@DBUnit(caseSensitiveTableNames = true, caseInsensitiveStrategy = Orthography.LOWERCASE)
public class WordRepositoryPostgresTest {

    private DataSource dataSource;
    private final ConnectionHolder connectionHolder = () -> dataSource.getConnection();

    @BeforeEach
    void setUpConnection() {
        this.dataSource = new PostgresTestDataSource();
    }


    @Test
    @DataSet("adapters/data/wordTable.json")
    void fetchesWord() {
        WordRepository repository = new WordRepositoryPostgres(dataSource);

        String actual = repository.fetchWordByNumber(27);

        assertThat(actual).isEqualTo("ARISE");
    }


    @Test
    @DataSet("adapters/data/threeWords.json")
    void returnsHighestWordNumber() {
        WordRepository repository = new WordRepositoryPostgres(dataSource);

        int actual = repository.highestWordNumber();

        assertThat(actual).isEqualTo(3);
    }
}
