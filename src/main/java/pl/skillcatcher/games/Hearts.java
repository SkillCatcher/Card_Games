package pl.skillcatcher.games;

import pl.skillcatcher.cards.*;

import java.util.Scanner;

public class Hearts extends Game implements Confirmable {


    private Player[] players;
    private int currentRound;
    private Card[] pool;
    private boolean heartsAllowed;

    Hearts() {
        Scanner scanner = new Scanner(System.in);
        this.heartsAllowed = false;
        this.numberOfPlayers = 4;
        this.deck = new Deck();
        this.currentRound = 1;
        this.pool = new Card[4];
        this.players = new Player[4];
        for (int i = 0; i < 4; i++) {
            if (i < numberOfPlayers) {
                System.out.println("Player " + (i+1) + " - name:\n");
                String name = scanner.nextLine();
                players[i] = new Player(name, i+1);
            } else {
                players[i] = new Player("A.I. #" + (i+1-numberOfPlayers),i+1 , PlayerStatus.AI);
            }
        }
    }

    public void setCardValues() {
        for (int i = 0; i < 52; i++) {
            if(deck.getACard(i).getId() == 43) {
                deck.getACard(i).setValue(13);
            } else if(deck.getACard(i).getColour() == CardColour.HEARTS) {
                deck.getACard(i).setValue(1);
            } else {
                deck.getACard(i).setValue(0);
            }
        }
    }

    public void startTheGame() {
        setCardValues();
        deck.shuffle();
        while (deck.getCards().size() > 0) {
            for (Player player : players) {
                deck.dealACard(player.getHand());
            }
        }

        for (Player player : players) {
            player.getHand().sortHandById();
        }

        if (gameRotation() != 0) {
            cardPassTurn();
        }

        currentPlayer = whoGotTwoOfClubs();
        for (Player player : players) {
            player.getHand().sortHandById();
            System.out.println(player.getName() + ":\n");
            player.getHand().displayHand();
            System.out.println("//////////////////////////////////////");
        }
        //currentSituation();
    }

    public void currentSituation() {
        System.out.println("Cards in the game so far:");
        displayPool();
        confirm();

        if (currentPlayer.getPlayerStatus().equals(PlayerStatus.USER)) {
            makeMove();
        } else {
            AI_Move();
        }
    }

    public void makeMove() {

        System.out.println(currentPlayer.getName() + " - your hand:");
        currentPlayer.getHand().displayHand();
    }

    public void AI_Move() {
        //trying to figure it out...
    }

    private void moveResult() {
        displayPool();
        System.out.println("This pool goes to "); //add winner name
    }

    public void printResults() {
        boolean endGame = false;
        System.out.println("Points after round " + currentRound + ":");
        for (int i = 0; i < players.length; i++) {
            System.out.println((i+1) + ". " + players[i].getName() + ": " + players[i].getPoints());
            if (players[i].getPoints() >= 100) {
                endGame = true;
            }
        }

        if (endGame) {
            printFinalScore();
        } else {
            currentRound++;
            resetGameSettings();
            startTheGame();
        }
    }

    public void printFinalScore() {
        Player winner;
        int[] score = new int[players.length];
        for (int i = 0; i < score.length; i++) {
            score[i] = players[i].getPoints();
        }
        //compare
    }

    public void confirm() {
        System.out.println("To continue, press Enter...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    private Player whoGotTwoOfClubs() {
        for (Player player : players) {
            for (Card card : player.getHand().getCards()) {
                if (card.getId() == 0) {
                    return player;
                }
            }
        }
        return null;
    }

    private void cardsPassExecute(Player receivingPlayer, Card[] cards) {
        for (Card card : cards) {
            receivingPlayer.getHand().getCards().add(card);
        }
    }

    private void cardPassTurn() {
        Scanner scanner = new Scanner(System.in);

        Card[][] cardSets = new Card[4][3];

        for (int i = 0; i < cardSets.length; i++) {
            System.out.println("Card Pass Turn for player: " + players[i].getName());
            confirm();
            System.out.println("Your hand:");
            players[i].getHand().displayHand();

            System.out.println("Choose 3 cards from the list by their number. They'll be passed to "
                    + players[( i + gameRotation() ) % 4].getName() + ": \n");

            for (int j = 0; j < cardSets[i].length; j++) {
                System.out.println("Number " + (j+1));
                int choice = scanner.nextInt();
                cardSets[i][j] = players[i].getHand().getACard(choice-1);
                players[i].getHand().getCards().remove(choice);
            }

            System.out.println("You've chosen: " + cardSets[i][0].getName() + ", " + cardSets[i][1].getName() + ", " +
                    cardSets[i][2].getName() + "\n");
            confirm();
        }

        for (int i = 0; i < cardSets.length; i++) {
            cardsPassExecute(players[(i + gameRotation()) % 4], cardSets[i]);
        }
    }

    private int gameRotation() {
        switch(currentRound%4) {
            case 1:
                return 1;
            case 2:
                return 3;
            case 3:
                return 2;
            default:
                return 0;
        }
    }

    private void enableHearts() {
        for (Card card : pool) {
            if (card.getColour() == CardColour.HEARTS) {
                heartsAllowed = true;
            }
        }
    }

    private boolean canBePlayed(Card card) {
        return true;
    }

    private void displayPool() {
        for (int i = 0; i < pool.length; i++) {
            if (pool[i] == null) {
                System.out.println((i+1) + ". ---");
            } else {
                System.out.println((i+1) + ". " + pool[i].getName());
            }
        }
    }

    private void resetGameSettings() {
        heartsAllowed = false;
        deck = new Deck();
        for (Player player : players) {
            player.setHand(new Hand());
        }
    }
}
