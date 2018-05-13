package pl.skillcatcher.games;

import pl.skillcatcher.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlackJack {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        List<Card> cardDeck = new ArrayList<Card>();
        List<Card> playerHand = new ArrayList<Card>();
        List<Card> dealerHand = new ArrayList<Card>();

        for (int i = 0; i < 52; i++) {
            cardDeck.add(new Card(i));
            if(i < 36) {
                cardDeck.get(i).setValue((i/4)+2);
            } else if (i < 48) {
                cardDeck.get(i).setValue(10);
            } else {
                cardDeck.get(i).setValue(11);
            }
            //System.out.println(cardDeck.get(i).getName() + ". Value: " + cardDeck.get(i).getValue());
        }

        dealACard(cardDeck, dealerHand);
        dealACard(cardDeck, playerHand);
        dealACard(cardDeck, dealerHand);
        dealACard(cardDeck, playerHand);
    }

    private static void dealACard(List<Card> deck, List<Card> receivingHand) {
        int randomCardInDeck = (int)Math.floor(Math.random()*deck.size());
        Card randomCard = deck.get(randomCardInDeck);
        deck.remove(randomCardInDeck);
        receivingHand.add(randomCard);
    }

    //player move (hit/stand)
    //computer (dealer) move (16/17)
    //result
}
