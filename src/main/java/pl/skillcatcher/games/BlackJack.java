package pl.skillcatcher.games;

import pl.skillcatcher.cards.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class BlackJack extends Game implements Confirmable, SetPlayers, CorrectInputCheck {

    private int roundsToPlay;
    private Player dealer;
    private ArrayList<Player> notFinishedPlayers;
    private ArrayList<Player> listOfPlayersToRemove;

    BlackJack() {
        this.deck = new Deck();
        this.numberOfHumanPlayers = inputWithCheck("Please choose a number of players " +
                "(between 1 and 7):", 1, 7);
        this.numberOfAllPlayers = numberOfHumanPlayers;
        this.roundsToPlay = inputWithCheck("Please choose a number of rounds you want to play " +
                "(between 1 and 100):", 1, 100);
        this.players = new Player[numberOfAllPlayers];
        createPlayers(numberOfHumanPlayers, numberOfAllPlayers, players);

        dealer = new Player("Dealer", -1, PlayerStatus.AI);
        notFinishedPlayers = new ArrayList<>();
        listOfPlayersToRemove = new ArrayList<>();
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
            deck.dealACard(player.getHand());
        }
        deck.dealACard(dealer.getHand());
        deck.dealACard(dealer.getHand());

        currentPlayer = players[roundsToPlay%numberOfHumanPlayers];
        addToListFromCurrentPlayer(players, notFinishedPlayers, currentPlayer);


        while (notFinishedPlayers.size() > 0) {
            for (Player player : notFinishedPlayers) {
                currentSituation(player);
            }
            removePlayersFromList(notFinishedPlayers, listOfPlayersToRemove);
        }
        System.out.println("\nDealer's turn...\n");
        AI_Move(dealer);
    }

    private void addToListFromCurrentPlayer(Player[] allPlayers, ArrayList<Player> list, Player current) {
        for (int i = current.getId(); i < allPlayers.length + current.getId(); i++) {
            list.add(allPlayers[i % allPlayers.length]);
        }
    }

    private void removePlayersFromList(ArrayList<Player> biggerList, ArrayList<Player> smallerList) {
        for (Player player : smallerList) {
            biggerList.remove(player);
        }
    }

    void currentSituation(Player player) {
        System.out.println(player.getName().toUpperCase() + " - IT'S YOUR TURN\n");
        confirm();

        displayOpponentsHands(player);

        System.out.println("You can see, that dealer's got a " +
                dealer.getCard(0).getName() + ".\n");

        decreaseAceValue(player);

        System.out.println("Your current hand: ");
        player.getHand().displayHand();
        player.getHand().displayPoints();

        if(failCheck(player)) {
            System.out.println("Oops... your currently have " + player.getHand().getPoints() +
                    " points, which is more than 21. You've lost...");

            listOfPlayersToRemove.add(player);
        } else {
            makeMove(player);
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
            if (!player.equals(yourPlayer)) {
                System.out.println("Hand of " + player.getName());
                player.getHand().displayHand();
                player.getHand().displayPoints();
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
                break;
            case 2:
                System.out.println("You've finished with " + player.getHand().getPoints() + " points.\n");
                confirm();
                listOfPlayersToRemove.add(player);
                break;
            default:
                break;
        }
    }

    void AI_Move(Player player) {
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
        ArrayList<Player> winners = new ArrayList<>();
        System.out.println("\nResults:\n");
        confirm();
        System.out.println(dealer.getName() + ": " + dealer.getHand().getPoints() + " points");

        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getHand().getPoints() + " points");

            if ( !failCheck(player) &&
                    (player.getHand().getPoints() > dealer.getHand().getPoints() || failCheck(dealer)) ) {
                winners.add(player);
                player.addPoints(1);
            } else if ( !failCheck(dealer) &&
                    (player.getHand().getPoints() < dealer.getHand().getPoints() || failCheck(player)) ) {
                dealer.addPoints(1);
            } else if (player.getHand().getPoints() == dealer.getHand().getPoints() && !failCheck(player)
                    && !failCheck(dealer)){
                winners.add(player);
                player.addPoints(1);
                dealer.addPoints(1);
            }
        }

        System.out.println("\nWinners of this round:");
        for (Player player : winners) {
            System.out.println(player.getName());
        }

        System.out.println("\nPoints so far:\n");
        int dealersPoints = dealer.getPoints() / numberOfHumanPlayers;
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getPoints() + " points");
        }
        System.out.println("\nDealers points: " + dealersPoints);

        roundsToPlay--;

        if (roundsToPlay > 0) {
            System.out.println("\n" + roundsToPlay + " rounds left...");

            deck = new Deck();
            for (Player player : players) {
                player.getCards().clear();
            }
            dealer.getCards().clear();
            notFinishedPlayers.clear();
            listOfPlayersToRemove.clear();
            confirm();
            startTheGame();
        } else {
            printFinalScore();
        }
    }

    void printFinalScore() {
        System.out.println("Final result:");
        confirm();
        dealer.setPoints(dealer.getPoints() / numberOfHumanPlayers);

        class PointsComparator implements Comparator<Player> {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o2.getPoints(), o1.getPoints());
            }
        }
        Arrays.sort(players, new PointsComparator());

        int scoreboardIndex = 1;
        for (Player player : players) {
            System.out.println(scoreboardIndex + ". " + player.getName() + ": " + player.getPoints() + " points");
            scoreboardIndex++;
        }

        System.out.println("\nDealers points: " + (dealer.getPoints() / numberOfHumanPlayers));

        Player winner = players[0];
        if (dealer.getPoints() > winner.getPoints()) {
            winner = dealer;
        }

        System.out.println("\nWINNER: " + winner.getName().toUpperCase() + "!!!");
    }
}
