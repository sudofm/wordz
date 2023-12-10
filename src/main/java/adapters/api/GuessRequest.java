package adapters.api;

import domain.Player;

public record GuessRequest(Player player, String guess) {
}
