package pl.skillcatcher.interfaces;

import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.PlayerStatus;

public interface PlayersCreator {
    default void createPlayers(Player[] players, String[] names) {
        for (int i = 0; i < players.length; i++) {
            if (i < names.length) {
                players[i] = new Player(names[i], i);
            } else {
                players[i] = new Player("AI #" + (i+1-names.length), i, PlayerStatus.AI);
            }
        }
    }
}
