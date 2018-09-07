package pl.skillcatcher.features;

import pl.skillcatcher.games.CardsOnTable;

import java.util.*;

public class Hand extends Deck {

    private List<Card> collectedCards;

    List<Card> getCollectedCards() {
        return collectedCards;
    }

    void setCollectedCards(List<Card> collectedCards) {
        this.collectedCards = collectedCards;
    }

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
        System.out.println("Dealing features from player's hand is not allowed. Try 'playACard' method instead.");
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
        System.out.println("Current points: " + getPoints() + "\n");
    }

    public void collectCards(CardsOnTable cardsOnTable) {
        for (int i = 0; i < cardsOnTable.getCards().size(); i++) {
            collectedCards.add(cardsOnTable.getCard(i));
        }
        cardsOnTable.clear();
    }

    private void sortCardsById() {
        getCards().sort(Comparator.comparing(Card::getId));
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

    public boolean containsOnlyOneColor(CardColour cardColour) {
        for (Card card : getCards()) {
            if (!(card.getColour().equals(cardColour))) {
                return false;
            }
        }
        return true;
    }

    public boolean containsCard(int cardId) {
        for (Card card : getCards()) {
            if (card.getId() == cardId) {
                return true;
            }
        }
        return false;
    }
}
