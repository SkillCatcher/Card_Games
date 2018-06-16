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

    Deck(int firstCardId, int lastCardId) {
        for (int i = firstCardId; i < lastCardId+1; i++) {
            cards.add(new Card(i));
        }
    }

    public Deck(List<Integer> customDeck) {
        for (Integer integer : customDeck) {
            cards.add(new Card(customDeck.get(integer)));
        }
    }

    public Deck(int[] customDeck) {
        for (int i : customDeck) {
            cards.add(new Card(i));
        }
    }

    public void dealACard(Hand hand) {
        Card topCard = cards.get(0);
        cards.remove(0);
        hand.getCards().add(topCard);
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
