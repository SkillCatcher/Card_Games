package pl.skillcatcher.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards = new ArrayList<>();
    public List<Card> getCards() {
        return cards;
    }
    public Card getACard(int index) {
        return cards.get(index);
    }
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Deck() {
        for (int i = 0; i < 52; i++) {
            cards.add(new Card(i));
        }
    }

    Deck(int firstCardId, int lastCardId) throws IllegalArgumentException {
        if (firstCardId > -1 && lastCardId < 52) {
            for (int i = firstCardId; i < lastCardId+1; i++) {
                cards.add(new Card(i));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Deck(int[] cardIds) {
        for (int i : cardIds) {
            cards.add(new Card(i));
        }
    }

    public void dealACard(Hand hand) throws NullPointerException {
        if (cards.size() > 0) {
            Card topCard = cards.get(0);
            cards.remove(0);
            hand.getCards().add(topCard);
        } else {
            throw new NullPointerException("Error - card not found");
        }
    }

    public void shuffle() {
        Collections.shuffle(getCards());
    }

    public void setCardValuesById(int minId, int maxId, int value) {
        for (int i = minId; i <= maxId; i++) {
            getACard(i).setValue(value);
        }
    }

    public void setSingleCardValueById(int id, int value) {
        getACard(id).setValue(value);
    }

    public void setCardValuesByColor(CardColour cardColour, int value) {
        for (Card card : getCards()) {
            if (card.getColour().equals(cardColour)) {
                card.setValue(value);
            }
        }
    }

    public void setCardValuesByNumber(CardNumber cardNumber, int value) {
        for (Card card : getCards()) {
            if (card.getNumber().equals(cardNumber)) {
                card.setValue(value);
            }
        }
    }

    public void setAllCardValues(int value) {
        for (Card card : getCards()) {
            card.setValue(value);
        }
    }
}
