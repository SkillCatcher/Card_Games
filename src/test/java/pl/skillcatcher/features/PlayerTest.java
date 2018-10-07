package pl.skillcatcher.features;

import org.junit.Test;
import pl.skillcatcher.asserts.CardsAssert;

import static org.junit.Assert.*;

public class PlayerTest {
    private Player player = new Player("", 0);

    @Test
    public void should_Add_Points() {
        player.setPoints(24);
        player.addPoints(6);

        assertEquals(30, player.getPoints());
    }

    @Test
    public void should_Get_A_Card() {
        Card comparableCard = new Card(45);
        Deck deck = new Deck(43, 51);
        Hand hand = new Hand();
        hand.setCards(deck.getCards());
        player.setHand(hand);

        new CardsAssert(comparableCard).isThisTheSameCard(player.getCard(2));
    }
}