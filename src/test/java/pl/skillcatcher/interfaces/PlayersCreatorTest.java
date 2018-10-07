package pl.skillcatcher.interfaces;

import org.junit.Test;
import pl.skillcatcher.asserts.PlayerAssert;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.PlayerStatus;

public class PlayersCreatorTest implements PlayersCreator {
    private Player[] players = new Player[4];
    private String[] names = {"Test player 1", "Test player 2", "Test player 3"};

    @Test
    public void should_Create_Players() {
        Player[] expectedPlayers = new Player[4];
        expectedPlayers[0] = new Player("Test player 1", 0);
        expectedPlayers[1] = new Player("Test player 2", 1);
        expectedPlayers[2] = new Player("Test player 3", 2);
        expectedPlayers[3] = new Player("AI #1", 3, PlayerStatus.AI);

        createPlayers(players, names);

        for (int i = 0; i < expectedPlayers.length; i++) {
            new PlayerAssert(expectedPlayers[i]).isTheSamePlayer(players[i]);
        }
    }

    @Test(expected = NullPointerException.class)
    public void should_Not_Create_Players_Without_Given_Names() {
        createPlayers(players, null);
    }

    @Test(expected = NullPointerException.class)
    public void should_Not_Create_Players_Without_Given_Players_Array() {
        createPlayers(null, names);
    }
}