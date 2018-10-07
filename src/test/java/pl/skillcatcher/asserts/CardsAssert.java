package pl.skillcatcher.asserts;

import pl.skillcatcher.features.Card;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CardsAssert {

    private Card card;
    private List<Card> cards;

    public CardsAssert(Card card) {
        this.card = card;
    }

    public CardsAssert(List<Card> cards) {
        this.cards = cards;
    }

    public CardsAssert isThisTheSameCard(Card card) {
        assertEquals(this.card.getName(), card.getName());
        assertEquals(this.card.getNumber(), card.getNumber());
        assertEquals(this.card.getColour(), card.getColour());
        assertEquals(this.card.getId(), card.getId());
        return this;
    }

    public CardsAssert areTheseTheSameCards(List<Card> cards) {
        if (this.cards.size() != cards.size()) {
            fail();
        } else {
            for (int i = 0; i < cards.size(); i++) {
                new CardsAssert(cards.get(i)).isThisTheSameCard(this.cards.get(i));
            }
        }

        return this;
    }
}
