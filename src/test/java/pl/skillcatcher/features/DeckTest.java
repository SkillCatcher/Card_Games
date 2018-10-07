package pl.skillcatcher.features;

import org.junit.Test;
import pl.skillcatcher.asserts.CardsAssert;
import static org.junit.Assert.*;

public class DeckTest {
    private Deck valuesSettingDeck = new Deck();

    @Test(expected = IllegalArgumentException.class)
    public void should_NOT_Create_Deck_With_Incorrect_Card_Ids() {
        Deck deck = new Deck(5, 88);
    }

    @Test
    public void should_Get_A_Correct_Card() {
        Card comparableCard = new Card(33);
        Deck deck = new Deck();

        new CardsAssert(comparableCard).isThisTheSameCard(deck.getACard(33));
    }

    @Test
    public void should_Create_Deck_With_Correct_Card_Ids() {
        Deck deck = new Deck(5, 28);
        Card firstCard = new Card(5);
        Card lastCard = new Card(28);

        new CardsAssert(firstCard).isThisTheSameCard(deck.getACard(0));
        new CardsAssert(lastCard).isThisTheSameCard(deck.getACard(deck.getCards().size()-1));
    }

    @Test
    public void should_Deal_A_Correct_Card() {
        Card comparableCard = new Card(5);
        Hand hand = new Hand();
        Deck deck = new Deck(5, 42);
        deck.dealACard(hand);
        new CardsAssert(comparableCard).isThisTheSameCard(hand.getACard(0));
    }

    @Test(expected = AssertionError.class)
    public void should_Shuffle_Deck() {
        Deck comparableDeck = new Deck();
        Deck deck = new Deck();
        deck.shuffle();
        for (int i = 0; i < 52; i++) {
            new CardsAssert(comparableDeck.getACard(i)).isThisTheSameCard(deck.getACard(i));
        }
    }

    @Test
    public void should_Set_Correct_Values_By_Id() {
        valuesSettingDeck.setCardValuesById(10, 38, 17);
        for (Card card : valuesSettingDeck.getCards()) {
            setValuesAssert(card.getId() > 9 && card.getId() < 39, 17,
                    0, card.getValue());
        }
    }

    @Test
    public void should_Set_Correct_Single_Value_By_Id() {
        valuesSettingDeck.setSingleCardValueById(8, -5);
        for (Card card : valuesSettingDeck.getCards()) {
            setValuesAssert(card.getId() == 8, -5,
                    0, card.getValue());
        }
    }

    @Test
    public void should_Set_Correct_Values_By_Color() {
        valuesSettingDeck.setCardValuesByColor(CardColour.CLUBS, 5555);
        for (Card card : valuesSettingDeck.getCards()) {
            setValuesAssert(card.getColour().equals(CardColour.CLUBS), 5555,
                    0, card.getValue());
        }
    }

    @Test
    public void should_Set_Correct_Values_By_Number() {
        valuesSettingDeck.setCardValuesByNumber(CardNumber.EIGHT, 24);
        for (Card card : valuesSettingDeck.getCards()) {
            setValuesAssert(card.getNumber().equals(CardNumber.EIGHT), 24,
                    0, card.getValue());
        }
    }

    @Test
    public void should_Set_Correct_All_Values() {
        valuesSettingDeck.setAllCardValues(777);
        for (Card card : valuesSettingDeck.getCards()) {
            assertEquals(777, card.getValue());
        }
    }

    private void setValuesAssert(boolean condition, int expectedValueWhenTrue,
                                  int expectedValueWhenFalse, int actualValue) {
        if (condition) {
            assertEquals(expectedValueWhenTrue, actualValue);
        } else {
            assertEquals(expectedValueWhenFalse, actualValue);
        }
    }
}