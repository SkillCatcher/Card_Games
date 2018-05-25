package pl.skillcatcher.games;

import pl.skillcatcher.cards.CardNumber;
import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Hand;

import java.util.Scanner;

public class BlackJack {

    private Deck deck;
    private Hand playersHand;
    private Hand dealersHand;
    private int playersWins;
    private int dealersWins;
    private int roundsToPlay;

    public BlackJack(int roundsToPlay) {
        this.deck = new Deck();
        this.playersHand = new Hand();
        this.dealersHand = new Hand();
        this.playersWins = 0;
        this.dealersWins = 0;
        this.roundsToPlay = roundsToPlay;
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
        setBlackJackCardValues();
        deck.shuffle();
        deck.dealACard(playersHand);
        deck.dealACard(dealersHand);
        deck.dealACard(playersHand);
        deck.dealACard(dealersHand);

        currentSituation();
    }

    private void currentSituation() {
        System.out.println("You can see, that dealer's got a " + dealersHand.getACard(0).getName() + ".\n");
        decreaseAceValue(playersHand);

        System.out.println("Your current hand: ");
        playersHand.displayHand();
        playersHand.displayPoints();

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
                        "Now it's dealer's turn...\n");
                dealersTurn();
                break;
            default:
                System.out.println("Wrong command - try again...\n");
                makeMove();
                break;
        }
    }

    private void dealersTurn() {
        confirm();
        decreaseAceValue(dealersHand);
        System.out.println("\nDealer currently has this hand: ");
        dealersHand.displayHand();
        dealersHand.displayPoints();

        if (dealersHand.getPoints() < 17) {
            System.out.println("Dealer draws another card...");
            deck.dealACard(dealersHand);
            dealersTurn();
        } else {
            System.out.println("Dealer ends game with " + dealersHand.getPoints() + " points");
            printResults();
        }
    }

    private void printResults() {
        confirm();
        int playerScore = playersHand.getPoints();
        int dealerScore = dealersHand.getPoints();
        String winner;
        String message;

        if( (playerScore > dealerScore && !failCheck(playersHand)) || (failCheck(dealersHand)) ) {
            playersWins++;
            winner = "PLAYER";
            message = "Congratulations!!!";
        } else if ( (dealerScore > playerScore && !failCheck(dealersHand)) || (failCheck(playersHand)) ) {
            dealersWins++;
            winner = "DEALER";
            message = "Better luck next time!";
        } else {
            winner = "NOBODY";
            message = "TIE GAME, Ladies ana Gentlemen!";
        }

        System.out.println("\nFinal score:\nPlayer: " + playerScore +
                "        " + "Dealer: " + dealerScore);
        System.out.println("\nThe winner is...  " + winner + "!!!" + "\n" + message + "\n");
        roundsToPlay--;

        if (roundsToPlay > 0) {
            System.out.println("\n" + roundsToPlay + " rounds left...");

            deck = new Deck();
            playersHand = new Hand();
            dealersHand = new Hand();
            confirm();
            startTheGame();
        } else {
            printFinalScore();
        }

    }

    private boolean failCheck(Hand hand) {
        return hand.getPoints() > 21;
    }

    private void decreaseAceValue(Hand hand) {
        for (int i = 0; i < hand.getCards().size(); i++) {
            if (hand.getPoints() > 21 &&
                    hand.getACard(i).getNumber().equals(CardNumber.ACE) &&
                    hand.getACard(i).getValue() != 1) {
                hand.getACard(i).setValue(1);
                break;
            }
        }
    }

    private void printFinalScore() {
        confirm();
        String winner;
        if(playersWins > dealersWins) {
            winner = "PLAYER";
        } else {
            winner = "DEALER";
        }
        System.out.println("\nFinal score:" +
                "\n  Player: " + playersWins + " wins" +
                "\n  Dealer: " + dealersWins + " wins" +
                "\n \n" +
                winner + " WINS THE GAME!!!");
    }

    private void confirm() {
        System.out.println("To continue, press Enter...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
