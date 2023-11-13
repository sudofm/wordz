package adapters.db;

import domain.WordRepository;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public class WordRepositoryPostgres implements WordRepository {

    private final Jdbi jdbi;
    public WordRepositoryPostgres(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    @Override
    public String fetchWordByNumber(int wordNumber) {
        return null;
    }

    @Override
    public int highestWordNumber() {
        return 0;
    }
}
