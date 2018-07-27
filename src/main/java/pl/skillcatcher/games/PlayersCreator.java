package pl.skillcatcher.games;

import pl.skillcatcher.cards.Player;
import pl.skillcatcher.cards.PlayerStatus;

public interface PlayersCreator {
    default void createPlayers(Player[] players, String[] names) {
        for (int i = 0; i < players.length; i++) {
            if (i < names.length) {
                players[i] = new Player(names[i], i);
            } else {
                players[i] = new Player("A.I. #" + (i+1-names.length), i, PlayerStatus.AI);
            }
        }
    }
}
