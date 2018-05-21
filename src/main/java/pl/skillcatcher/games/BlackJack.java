package pl.skillcatcher.games;

import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Hand;

public class BlackJack {

    private Deck deck;
    private Hand playersHand;
    private Hand dealersHand;

    public BlackJack() {
        this.deck = new Deck();
        this.playersHand = new Hand();
        this.dealersHand = new Hand();
        this.setBlackJackCardValues();
    }

    private void setBlackJackCardValues() {
        for (int i = 0; i < 52; i++) {
            if(i < 36) {
                deck.getACard(i).setValue((i/4)+2);
            } else if (i < 48) {
                deck.getACard(i).setValue(10);
            } else {
                deck.getACard(i).setValue(11);
            }
        }
    }

    public void startTheGame() {
        deck.shuffle();
        deck.dealACard(playersHand);
        deck.dealACard(dealersHand);
        deck.dealACard(playersHand);
        deck.dealACard(dealersHand);

        System.out.println("You can see, that dealer's got a " + dealersHand.getACard(0) + ".");
    }

    private void currentSituation() {
        playersHand.displayHand();

        if(playersHand.getPoints() > 21) {
            System.out.println("Oops... your currently have " + playersHand.getPoints() +
                    " points, which is more than 21. You've lost...");
        } else {
            //decision
        }
    }
}
