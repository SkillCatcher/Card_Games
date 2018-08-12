package pl.skillcatcher.games;

import pl.skillcatcher.features.Deck;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.exceptions.GameFlowException;

public abstract class Game {

    private Deck deck;
    private int currentRound;
    private int numberOfHumanPlayers;
    private int numberOfAllPlayers;
    private Player currentPlayer;
    private Player[] players;
    private UserInteraction userInteraction = new UserInteraction();
    private GameStatus gameStatus;

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

    public UserInteraction getUserInteraction() {
        return userInteraction;
    }

    public void setUserInteraction(UserInteraction userInteraction) {
        this.userInteraction = userInteraction;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public abstract void setUpGame() throws GameFlowException;
    public abstract void dealCards();
    public abstract void startTheGame() throws GameFlowException;
    public abstract void currentSituation(Player player) throws GameFlowException;
    public abstract void makeMove(Player player) throws GameFlowException;
    public abstract void virtualPlayerMove(Player playerAI) throws GameFlowException;
    public abstract void printResults() throws GameFlowException;
    public abstract void printFinalScore() throws GameFlowException;

}
