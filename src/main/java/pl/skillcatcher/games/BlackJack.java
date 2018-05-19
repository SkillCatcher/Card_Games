package pl.skillcatcher.games;

import pl.skillcatcher.cards.Card;
import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlackJack {
    public static void main(String[] args) {

        Deck cardDeck = new Deck();
        Hand playerHand = new Hand();
        Hand dealerHand = new Hand();

        for (int i = 0; i < 52; i++) {
            if(i < 36) {
                cardDeck.getCards().get(i).setValue((i/4)+2);
            } else if (i < 48) {
                cardDeck.getCards().get(i).setValue(10);
            } else {
                cardDeck.getCards().get(i).setValue(11);
            }
        }

        cardDeck.shuffle();
        cardDeck.dealACard(dealerHand);
        cardDeck.dealACard(playerHand);
        cardDeck.dealACard(dealerHand);
        cardDeck.dealACard(playerHand);

        System.out.println("You can see, that dealer's got a " + dealerHand.getCards().get(0).getName() + ".");

        playersChoiceMenu(playerHand, cardDeck, dealerHand);
        printResult(playerHand, dealerHand);

    }

    private static void playersChoiceMenu(Hand playersHand, Deck deck, Hand dealersHand) {

        System.out.println("Your current hand: ");

        for (int i = 0; i < playersHand.getCards().size(); i++) {
            System.out.println(i+1 + ". " + playersHand.getCards().get(i).getName());
        }

        System.out.println("Your current points: " + playersHand.getPoints());
        System.out.println("");

        if(playersHand.getPoints() > 21) {
            System.out.println("Oops... your currently have " + playersHand.getPoints() +
                    " points, which is more than 21. You've lost");
        } else {
            playersChoice(playersHand, deck, dealersHand);
        }

    }

    private static void playersChoice(Hand playersHand, Deck deck, Hand dealersHand) {
        System.out.println("Do you want a hit or do you want to stay? [Press 1 or 2, and confirm with ENTER]");
        System.out.println("1 - Hit me!");
        System.out.println("2 - I'm good - I'll stay.");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if(choice == 1) {
            deck.dealACard(playersHand);
            playersChoiceMenu(playersHand, deck, dealersHand);
        } else if (choice == 2) {
            System.out.println("You've finished with " + playersHand.getPoints() + " points.");
            System.out.println("Now it's dealer's turn...");

            dealersTurn(dealersHand, deck);

        } else {
            System.out.println("Wrong command - try again...");
            System.out.println("");
            playersChoice(playersHand, deck, dealersHand);
        }

    }

    private static void dealersTurn(Hand dealersHand, Deck deck) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Dealer currently has this hand: ");

        for (int i = 0; i < dealersHand.getCards().size(); i++) {
            System.out.println(i+1 + ". " + dealersHand.getCards().get(i).getName());
        }

        System.out.println("");
        System.out.println("Dealers current points: " + dealersHand.getPoints());
        System.out.println("Press ENTER");
        String confirm = scan.nextLine();

        if (dealersHand.getPoints() < 17) {
            System.out.println("Dealer draws another card...");
            System.out.println("Press ENTER");
            confirm = scan.nextLine();
            deck.dealACard(dealersHand);
            dealersTurn(dealersHand, deck);
        } else {
            System.out.println("Dealer ends game with " + dealersHand.getPoints() + " points");
        }
    }

    private static void printResult(Hand player, Hand dealer) {
        int playerScore = player.getPoints();
        int dealerScore = dealer.getPoints();
        String winner;
        String message;
        if(playerScore > dealerScore) {
            winner = "PLAYER";
            message = "Congratulations";
        } else {
            winner = "DEALER";
            message = "Better luck next time";
        }

        System.out.println("Final score:\nPlayer: " + playerScore + "        " + "Dealer: " + dealerScore);
        System.out.println("The winner is...  " + winner + "!!!" + "\n" + message);
    }
}
