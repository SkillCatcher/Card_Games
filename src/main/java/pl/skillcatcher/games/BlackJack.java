package pl.skillcatcher.games;

import pl.skillcatcher.cards.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class BlackJack extends Game implements Confirmable, SetPlayers, CorrectInputCheck {

    private int roundsToPlay;

    BlackJack() {
        this.deck = new Deck();
        this.numberOfHumanPlayers = inputWithCheck("Please choose a number of HUMAN players " +
                "(between 1 and 7):", 1, 7);
        this.numberOfAllPlayers = numberOfHumanPlayers + 1;
        this.roundsToPlay = inputWithCheck("Please choose a number of rounds you want to play " +
                "(between 1 and 100):", 1, 100);
        this.players = new Player[numberOfAllPlayers];
        createPlayers(numberOfHumanPlayers, numberOfAllPlayers, players);
    }

    void setCardValues() {
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

    void startTheGame() {
        setCardValues();
        deck.shuffle();
        for (Player player : players) {
            deck.dealACard(player.getHand());
        }

        currentPlayer = players[roundsToPlay%numberOfHumanPlayers];
        currentSituation(currentPlayer);
    }

    void currentSituation(Player player) {
        System.out.println(player.getName().toUpperCase() + " - IT'S YOUR TURN\n");

        displayOpponentsHands(player);

        System.out.println("\nYou can see, that dealer's got a " +
                players[numberOfHumanPlayers].getCard(0).getName() + ".\n");

        decreaseAceValue(player);

        System.out.println("Your current hand: ");
        player.getHand().displayHand();
        player.getHand().displayPoints();

        if(failCheck(player)) {
            System.out.println("Oops... your currently have " + player.getHand().getPoints() +
                    " points, which is more than 21. You've lost...");
            //printResults();
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else {
            makeMove(currentPlayer);
        }
    }

    private boolean failCheck(Player player) {
        return player.getHand().getPoints() > 21;
    }

    private void decreaseAceValue(Player player) {
        for (int i = 0; i < player.getCards().size(); i++) {
            if (failCheck(player) && player.getCard(i).getNumber().equals(CardNumber.ACE) &&
                    player.getCard(i).getValue() != 1) {
                player.getCard(i).setValue(1);
                break;
            }
        }
    }

    private void displayOpponentsHands(Player yourPlayer) {
        for (Player player : players) {
            if (!(player.equals(yourPlayer) && player.equals(players[numberOfHumanPlayers]))) {
                System.out.println("\nHand of " + player.getName());
                player.getHand().displayHand();
            }
        }
    }

    void makeMove(Player player) {
        int choice = inputWithCheck("Do you want a hit or do you want to stay? " +
                "[Press 1 or 2, and confirm with ENTER]\n" +
                "1 - Hit me! (Draw another card)\n" +
                "2 - I'm good - I'll stay (End your turn)", 1, 2);

        switch (choice) {
            case 1:
                deck.dealACard(player.getHand());
                currentSituation(currentPlayer); /////////////////NEXT PLAYER
                break;
            case 2:
                System.out.println("You've finished with " + player.getHand().getPoints() + " points.\n");
                //wyłącz z gry
                //next player
                //jeśli nikt nie został, to tura dealera
                AI_Move(player);
                break;
            default:
                break;
        }
    }

    void AI_Move(Player player) {
        System.out.println("\nDealer's turn...\n");
        confirm();
        decreaseAceValue(player);
        System.out.println("\nDealer currently has this hand: ");
        player.getHand().displayHand();
        player.getHand().displayPoints();

        if (player.getHand().getPoints() < 17) {
            System.out.println("Dealer draws another card...");
            deck.dealACard(player.getHand());
            AI_Move(player);
        } else {
            System.out.println("Dealer ends game with " + player.getHand().getPoints() + " points");
            printResults();
        }
    }

    void printResults() {
        Player dealer = players[players.length-1];
        ArrayList<Player> winners = new ArrayList<>();
        System.out.println("\nResults:\n");
        confirm();
        System.out.println(dealer.getName() + ": " + dealer.getHand().getPoints() + " points");

        for (int i = 0; i < numberOfHumanPlayers; i++) {
            System.out.println(players[i].getName() + ": " + players[i].getHand().getPoints() + " points");
            if (players[i].getHand().getPoints() > dealer.getHand().getPoints()) {
                winners.add(players[i]);
                players[i].addPoints(1);
            } else if (players[i].getHand().getPoints() < dealer.getHand().getPoints()) {
                dealer.addPoints(1);
            } else {
                winners.add(players[i]);
                players[i].addPoints(1);
                dealer.addPoints(1);
            }
        }

        System.out.println("Winners of this round:");
        for (Player player : winners) {
            System.out.println(player.getName());
        }

        System.out.println("\nPoints so far:\n");
        int dealersPoints = dealer.getPoints() / numberOfHumanPlayers;
        for (Player player : players) {
            System.out.println(player.getName() + ":");
            if (player.equals(dealer)) {
                System.out.println(dealersPoints + "\n");
            } else {
                System.out.println(player.getPoints() + "\n");
            }
        }

        roundsToPlay--;

        if (roundsToPlay > 0) {
            System.out.println("\n" + roundsToPlay + " rounds left...");

            deck = new Deck();
            for (Player player : players) {
                player.getCards().clear();
            }
            confirm();
            startTheGame();
        } else {
            printFinalScore();
        }
    }

    void printFinalScore() {
        confirm();
        Player dealer = players[players.length-1];
        dealer.setPoints(dealer.getPoints() / numberOfHumanPlayers);

        class PointsComparator implements Comparator<Player> {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o2.getPoints(), o1.getPoints());
            }
        }
        Arrays.sort(players, new PointsComparator());

        System.out.println("Final result:");
        int scoreboardIndex = 1;
        for (Player player : players) {
            System.out.println(scoreboardIndex + ". " + player.getName() + ": " + player.getPoints() + " points");
        }

        System.out.println("WINNER: " + players[0].getName().toUpperCase() + "!!!");
    }

    @Override
    public void createPlayers(int numberOfHumanPlayers, int numberOfAllPlayers, Player[] players) {
        for (int i = 0; i < numberOfAllPlayers; i++) {
            if (i < numberOfHumanPlayers) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Player " + (i+1) + " - name:\n");
                String name = scanner.nextLine();
                players[i] = new Player(name, i);
            } else {
                players[i] = new Player("Dealer", i, PlayerStatus.AI);
            }
        }
    }
}
