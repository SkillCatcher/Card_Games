package pl.skillcatcher.cards;

import java.util.ArrayList;
import java.util.Arrays;
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
        sortCardsByColour();
        for (int i = 0; i < getCards().size(); i++) {
            System.out.println(i+1 + ". " + getACard(i).getName());
        }
    }

    public void displayPoints() {
        System.out.println("\nCurrent points: " + getPoints() + "\n");
    }

    public void collectCards(Card[] pool) {
        collectedCards.addAll(Arrays.asList(pool));
        for (int i = 0; i < pool.length; i++) {
            Arrays.asList(pool).set(i, null);
        }
    }

    public void collectCards(Card card) {
        collectedCards.add(card);
    }

    public void clearCollectedCards() {
        collectedCards = new ArrayList<>();
    }

    private void sortCardsById() {
        class IdComparator implements Comparator<Card> {
            public int compare(Card o1, Card o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        }
        getCards().sort(new IdComparator());
    }

    private void sortCardsByColour() {
        ArrayList<CardColour> cardColours = new ArrayList<>();
        ArrayList<Card> sortedHand = new ArrayList<>();

        cardColours.add(CardColour.CLUBS);
        cardColours.add(CardColour.DIAMONDS);
        cardColours.add(CardColour.HEARTS);
        cardColours.add(CardColour.SPADES);

        for (CardColour cardColour : cardColours) {
            Hand temp = new Hand();

            for (Card card : getCards()) {
                if (card.getColour().equals(cardColour)) {
                    temp.getCards().add(card);
                }
            }
            temp.sortCardsById();
            sortedHand.addAll(temp.getCards());
        }
        setCards(sortedHand);
    }
}
