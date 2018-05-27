package pl.skillcatcher.games;

import pl.skillcatcher.cards.Card;
import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Player;
import pl.skillcatcher.cards.PlayerStatus;

import java.util.Scanner;

public class Hearts implements Game {

    private Deck deck;
    private Player[] players;
    private int numberOfPlayers;
    private int currentRound;
    private Player currentPlayer;
    private Card[] pool;

    public Hearts(int numberOfPlayers) {
        Scanner scanner = new Scanner(System.in);
        this.numberOfPlayers = numberOfPlayers;
        this.deck = new Deck();
        this.currentRound = 1;
        this.pool = new Card[4];
        this.players = new Player[4];
        for (int i = 0; i < 4; i++) {
            if (i < numberOfPlayers) {
                System.out.println("Player " + (i+1) + " - name:\n");
                players[i] = new Player(scanner.nextLine());
            } else {
                players[i] = new Player("A.I. #" + (i+1), PlayerStatus.AI);
            }
        }
    }

    public void setCardValues() {
        for (int i = 0; i < 52; i++) {
            deck.getACard(i).setValue((i/4)+2);
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
        currentPlayer = whoGotTwoOfClubs();
        currentSituation();
    }

    public void currentSituation() {

    }

    public void makeMove() {

    }

    public void AI_Move() {

    }

    public void printResults() {

    }

    public void printFinalScore() {

    }

    public void confirm() {

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
}
