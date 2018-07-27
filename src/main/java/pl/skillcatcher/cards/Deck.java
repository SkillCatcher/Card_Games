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

    public Deck(int firstCardId, int lastCardId) throws IllegalArgumentException {
        if (firstCardId > -1 && lastCardId < 52) {
            for (int i = firstCardId; i < lastCardId+1; i++) {
                cards.add(new Card(i));
            }
        } else {
            throw new IllegalArgumentException();
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
//        List<Card> temp = new ArrayList<>();
//        final int size = cards.size();
//        for (int i = 0; i < size; i++) {
//            int randomCardId = (int)Math.floor(Math.random()*cards.size());
//            Card randomCard = cards.get(randomCardId);
//            cards.remove(randomCardId);
//            temp.add(randomCard);
//        }
//
//        setCards(temp);
    }
}
