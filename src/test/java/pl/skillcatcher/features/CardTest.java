package pl.skillcatcher.features;

import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {

    @Test(expected = IllegalArgumentException.class)
    public void should_NOT_Create_Card_With_Incorrect_Id() {
        Card card = new Card(69);
    }

    @Test
    public void should_Create_Card_With_Correct_Id() {
        Card card = new Card(15);
        String cardName = card.getName();

        assertEquals(cardName, "Five of Spades");
        assertEquals(card.getColour(), CardColour.SPADES);
        assertEquals(card.getNumber(), CardNumber.FIVE);
    }

    @Test
    public void shouldSetCorrectValue() {
        Card card = new Card(6);
        card.setValue(99);

        assertEquals(card.getValue(), 99);
    }
}