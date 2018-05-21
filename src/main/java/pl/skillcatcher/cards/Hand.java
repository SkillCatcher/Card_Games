package pl.skillcatcher.cards;

public class Hand extends Deck {

    public Hand() {
        super(0, -1);
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
        System.out.println("Your current hand: ");
        for (int i = 0; i < getCards().size(); i++) {
            System.out.println(i+1 + ". " + getACard(i).getName());
        }

        System.out.println("Your current points: " + getPoints());
        System.out.println("");
    }
}
