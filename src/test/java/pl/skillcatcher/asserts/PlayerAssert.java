package pl.skillcatcher.asserts;

import pl.skillcatcher.features.Player;

import static org.junit.Assert.assertEquals;

public class PlayerAssert {

    private Player player;

    public PlayerAssert(Player player) {
        this.player = player;
    }

    public PlayerAssert isTheSamePlayer(Player player) {
        assertEquals(this.player.getName(), player.getName());
        assertEquals(this.player.getId(), player.getId());
        assertEquals(this.player.getPlayerStatus(), player.getPlayerStatus());
        return this;
    }
}
