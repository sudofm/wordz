package adapters.api;

import domain.GuessResult;
import domain.Letter;
import domain.Score;

public class GuessHttpResponseMapper {
    GuessHttpResponse from(GuessResult result) {
        Score score = result.score();
        return new GuessHttpResponse(toEndpointResultsFormat(score), result.isGameOver());
    }

    private String toEndpointResultsFormat(Score score) {
        var results = new StringBuilder();
        score.letters().forEach(letter -> results.append(convert(letter)));
        return results.toString();
    }

    private char convert(Letter letter) {
        return switch(letter) {
            case CORRECT -> 'C';
            case PART_CORRECT -> 'P';
            case INCORRECT -> 'X';
        };
    }
}
