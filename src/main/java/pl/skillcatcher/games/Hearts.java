package pl.skillcatcher.games;

import pl.skillcatcher.cards.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Hearts extends Game implements Confirmable, SetPlayersNames {


    private Player[] players;
    private int currentRound;
    private Card[] pool;
    private boolean heartsAllowed;

    Hearts() {

        this.heartsAllowed = false;
        this.numberOfPlayers = 4;
        this.deck = new Deck();
        this.currentRound = 1;
        this.pool = new Card[4];
        this.players = new Player[4];
        setNames(numberOfPlayers, players);
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

        if (gameRotation() != 0) {
            cardPassTurn();
        }

        currentPlayer = whoGotTwoOfClubs();

        while (currentPlayer.getCards().size() > 0) {
            for (int i = currentPlayer.getId(); i < currentPlayer.getId() + 4; i++) {
                currentSituation(players[i%4]);
            }
            moveResult();
        }

        printResults();
    }

    public void currentSituation(Player player) {

        System.out.println(player.getName().toUpperCase() + " - IT'S YOUR TURN"
                + "\nOther players - no peeking :)\n");
        confirm();
        System.out.println("Cards in the game so far:");
        displayPool();
        confirm();

        if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
            makeMove(player);
        } else {
            AI_Move();
        }
    }

    public void makeMove(Player player) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(player.getName() + " - your hand:");
        player.getHand().displayHand();

        System.out.println("\nPick a card: ");
        int choice = scanner.nextInt();
        //CHECK IF CARD ALLOWED
        pool[player.getId()] = player.getHand().playACard(choice-1);
    }

    public void AI_Move() {
        //trying to figure it out...
    }

    private void moveResult() {
        System.out.println("Cards in game:");
        displayPool();
        Player winner = poolWinner();
        System.out.println("This pool goes to " + winner.getName());
        confirm();
        checkForEnablingHearts();
        winner.getHand().collectCards(pool);
        currentPlayer = winner;
    }

    private Player poolWinner() {
        int winnerIndex = currentPlayer.getId();
        Card winningCard = pool[currentPlayer.getId()];
        CardColour validColour = pool[currentPlayer.getId()].getColour();
        for (int i = 1; i < pool.length; i++) {
            if(pool[(currentPlayer.getId()+i)%4].getColour().equals(validColour)) {
                if(pool[(currentPlayer.getId()+i)%4].getId() > winningCard.getId()) {
                    winningCard = pool[(currentPlayer.getId()+i)%4];
                    winnerIndex = (currentPlayer.getId()+i)%4;
                }
            }
        }
        return players[winnerIndex];
    }

    private void updatePoints() {
        for (Player player : players) {
            for (Card card : player.getCollectedCards()) {
                player.addPoints(card.getValue());
            }
        }
    }

    public void printResults() {
        boolean endGame = false;
        updatePoints();
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
//        for (Player player : players) {
//            player.setPoints((int)Math.floor(Math.random()*100));
//        }
        sortPlayersByPoints();
        Player winner = players[0];
        int i = 0;
        for (Player player : players) {
            System.out.println((i+1) + ". " + player.getName() + ": " + player.getPoints());
            i++;
        }
        System.out.println("\nWinner - " + winner.getName());
    }

    private void sortPlayersByPoints() {
        class pointsComparator implements Comparator<Player> {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o1.getPoints(), o2.getPoints());
            }
        }
        Arrays.asList(players).sort(new pointsComparator());
    }

    private Player whoGotTwoOfClubs() {
        for (Player player : players) {
            for (Card card : player.getCards()) {
                if (card.getId() == 0) {
                    return player;
                }
            }
        }
        return null;
    }

    private void cardsPassExecute(Player receivingPlayer, ArrayList<Card> cards) {
        for (Card card : cards) {
            receivingPlayer.getCards().add(card);
        }
    }

    private void cardPassChoice(Player player, ArrayList<Card> cardSet) {
        Scanner scanner = new Scanner(System.in);
        final int numberOfCardsToPass = 3;
        int numberOfCardsChosen = 0;

        while (numberOfCardsChosen < numberOfCardsToPass) {
            boolean cardPickedFirstTime = true;

            if (numberOfCardsChosen > 0) {
                System.out.println("Your choices so far...");
                for (int i = 0; i < numberOfCardsChosen; i++) {
                    System.out.println((i+1) + ". " + cardSet.get(i).getName());
                }
            }

            System.out.println("Choose card number " + (numberOfCardsChosen+1) + ":");
            int choice = scanner.nextInt();

            for (int i = 0; i < numberOfCardsChosen; i++) {
                if (cardSet.get(i).equals(player.getCard(choice-1))) {
                    cardPickedFirstTime = false;
                    break;
                }
            }

            if (cardPickedFirstTime) {
                cardSet.add(player.getCard(choice - 1));
                numberOfCardsChosen++;
            } else {
                System.out.println("Choice of " + player.getCard(choice-1).getName()
                        + " was cancelled.");
                cardSet.remove(player.getCard(choice-1));
                numberOfCardsChosen--;
            }
        }
    }

    private void cardPassTurn() {
        ArrayList<ArrayList<Card>> cardSets = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            cardSets.add(i, new ArrayList<>());

            System.out.println("Card Pass Turn for player: " + players[i].getName());
            confirm();

            System.out.println("Your hand:");
            players[i].getHand().displayHand();

            System.out.println("Choose 3 cards from the list by their number (if you want to reverse the pick, " +
                    "simply pick the same card again). They'll be passed to "
                    + players[( i + gameRotation() ) % 4].getName() + ": \n");

            cardPassChoice(players[i], cardSets.get(i));
            printCardChoices(cardSets.get(i));
            confirm();

            for (Card card : cardSets.get(i)) {
                players[i].getCards().remove(card);
            }
        }

        for (int i = 0; i < 4; i++) {
            cardsPassExecute(players[(i + gameRotation()) % 4], cardSets.get(i));
        }
    }

    private void printCardChoices(ArrayList<Card> cardSet) {
        System.out.println("You've chosen: ");
        for (Card card : cardSet) System.out.println(card.getName());
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

    private void checkForEnablingHearts() {
        for (Card card : pool) {
            if (card.getColour().equals(CardColour.HEARTS)) {
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
                System.out.println((i+1) + ". " + pool[i].getName() + " (" + players[i].getName() + ")");
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
