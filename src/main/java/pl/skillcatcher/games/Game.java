package pl.skillcatcher.games;

import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Player;

abstract class Game {

    private Deck deck;
    private int currentRound;
    private int numberOfHumanPlayers;
    private int numberOfAllPlayers;
    private Player currentPlayer;
    private Player[] players;

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getNumberOfHumanPlayers() {
        return numberOfHumanPlayers;
    }

    public void setNumberOfHumanPlayers(int numberOfHumanPlayers) {
        this.numberOfHumanPlayers = numberOfHumanPlayers;
    }

    public int getNumberOfAllPlayers() {
        return numberOfAllPlayers;
    }

    public void setNumberOfAllPlayers(int numberOfAllPlayers) {
        this.numberOfAllPlayers = numberOfAllPlayers;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    abstract void setUpGame();
    abstract void setCardValues();
    abstract void startTheGame();
    abstract void currentSituation(Player player);
    abstract void makeMove(Player player);
    abstract void virtualPlayerMove(Player playerAI);
    abstract void printResults();
    abstract void printFinalScore();

}
