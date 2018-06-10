package pl.skillcatcher.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Hand extends Deck {

    private ArrayList<Card> collectedCards;

    public Hand() {
        super(1, 0);
        collectedCards = new ArrayList<>();
    }

    public int getPoints() {
        int sum = 0;
        for(Card card : getCards()) {
            sum += card.getValue();
        }

        return sum;
    }

    @Override
    public void dealACard(Hand hand) {
        System.out.println("Dealing cards from player's hand is not allowed. Try 'playACard' method instead.");
    }

    public Card playACard(int cardIndex) {
        Card chosenCard = getCards().get(cardIndex);
        getCards().remove(cardIndex);
        return chosenCard;
    }

    public void displayHand() {
        for (int i = 0; i < getCards().size(); i++) {
            System.out.println(i+1 + ". " + getACard(i).getName());
        }
    }

    public void displayPoints() {
        System.out.println("\nCurrent points: " + getPoints() + "\n");
    }

    public void collectCards(ArrayList<Card> pool) {
        collectedCards.addAll(pool);
    }

    public void collectCards(Card card) {
        collectedCards.add(card);
    }

    public void clearCollectedCards() {
        collectedCards = new ArrayList<>();
    }

    public void sortHandById() {
        class IdComparator implements Comparator<Card> {
            public int compare(Card o1, Card o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        }
        getCards().sort(new IdComparator());
    }
}
