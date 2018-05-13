package pl.skillcatcher.games;

import pl.skillcatcher.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlackJack {
    public static void main(String[] args) {

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

        System.out.println("You can see, that dealer's got a " + dealerHand.get(0).getName() + ".");

        playersChoiceMenu(playerHand, cardDeck);

    }

    private static void dealACard(List<Card> deck, List<Card> receivingHand) {
        int randomCardInDeck = (int)Math.floor(Math.random()*deck.size());
        Card randomCard = deck.get(randomCardInDeck);
        deck.remove(randomCardInDeck);
        receivingHand.add(randomCard);
    }

    private static int getTotalPoints(List<Card> hand) {
        int sum = 0;

        for(Card card : hand) {
            sum += card.getValue();
        }

        return sum;
    }

    private static void playersChoiceMenu(List<Card> playersHand, List<Card> deck) {

        System.out.println("Your current hand: ");

        for (int i = 0; i < playersHand.size(); i++) {
            System.out.println(i+1 + ". " + playersHand.get(i).getName());
        }

        System.out.println("Your current points: " + getTotalPoints(playersHand));
        System.out.println("");

        //System.out.println("You can see, that dealer's got a " + dealersHand.get(0).getName() + ".");

        if(getTotalPoints(playersHand) > 21) {
            System.out.println("Oops... your currently have " + getTotalPoints(playersHand) +
                    " points, which is more than 21. You've lost");
        } else {
            playersChoice(playersHand, deck);
        }

    }

    private static void playersChoice(List<Card> playersHand, List<Card> deck) {
        System.out.println("Do you want a hit or do you want to stay? [Press 1 or 2, and confirm with ENTER]");
        System.out.println("1 - Hit me!");
        System.out.println("2 - I'm good - I'll stay.");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if(choice == 1) {
            dealACard(deck, playersHand);
            playersChoiceMenu(playersHand, deck);
        } else if (choice == 2) {
            System.out.println("You've finished with " + getTotalPoints(playersHand) + " points.");
            System.out.println("Now it's dealer's turn...");
        } else {
            System.out.println("Wrong command - try again...");
            System.out.println("");
            playersChoice(playersHand, deck);
        }

    }

    //player move (hit/stand)
    //computer (dealer) move (16/17)
    //result
}
