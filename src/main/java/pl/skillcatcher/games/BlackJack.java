package pl.skillcatcher.games;

import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Hand;

import java.util.Scanner;

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

        System.out.println("You can see, that dealer's got a " + dealersHand.getACard(0).getName() + ".");
        currentSituation();
    }

    private void currentSituation() {
        System.out.println("Your current hand: ");
        playersHand.displayHand();

        if(playersHand.getPoints() > 21) {
            System.out.println("Oops... your currently have " + playersHand.getPoints() +
                    " points, which is more than 21. You've lost...");
            printResults();
        } else {
            makeMove();
        }
    }

    private void makeMove() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want a hit or do you want to stay? [Press 1 or 2, and confirm with ENTER]\n" +
                "1 - Hit me! (Draw another card)\n" +
                "2 - I'm good - I'll stay (End your turn)\n");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                deck.dealACard(playersHand);
                currentSituation();
                break;
            case 2:
                System.out.println("You've finished with " + playersHand.getPoints() + " points.\n" +
                        "Now it's dealer's turn...");
                dealersTurn();
                break;
            default:
                System.out.println("Wrong command - try again...\n");
                makeMove();
                break;
        }
    }

    private void dealersTurn() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Dealer currently has this hand: ");
        dealersHand.displayHand();
        System.out.println("Press ENTER to continue...");
        scan.nextLine();

        if (dealersHand.getPoints() < 17) {
            System.out.println("Dealer draws another card...\n" +
                    "Press ENTER to continue...");
            scan.nextLine();
            deck.dealACard(dealersHand);
            dealersTurn();
        } else {
            System.out.println("Dealer ends game with " + dealersHand.getPoints() + " points");
            printResults();
        }
    }

    private void printResults() {
        int playerScore = playersHand.getPoints();
        int dealerScore = dealersHand.getPoints();
        String winner;
        String message;

        if( (playerScore > dealerScore && !playersHand.failCheck()) || (dealersHand.failCheck()) ) {
            winner = "PLAYER";
            message = "Congratulations!!!";
        } else if ( (dealerScore > playerScore && !dealersHand.failCheck()) || (playersHand.failCheck()) ) {
            winner = "DEALER";
            message = "Better luck next time!";
        } else {
            winner = "NOBODY";
            message = "TIE GAME, Ladies ana Gentlemen!";
        }

        System.out.println("Final score:\nPlayer: " + playerScore +
                "        " + "Dealer: " + dealerScore);
        System.out.println("The winner is...  " + winner + "!!!" + "\n" + message);
    }
}
