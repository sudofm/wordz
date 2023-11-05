package domain;

public interface WordRepository {
    String fetchWordByNumber(int wordNumber);

    int highestWordNumber();
}
