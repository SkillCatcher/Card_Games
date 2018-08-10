package pl.skillcatcher.games;

import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Player;
import pl.skillcatcher.cards.UserAttention;
import pl.skillcatcher.exceptions.GameFlowException;

abstract class Game {

    private Deck deck;
    private int currentRound;
    private int numberOfHumanPlayers;
    private int numberOfAllPlayers;
    private Player currentPlayer;
    private Player[] players;
    private UserAttention userAttention = new UserAttention();
    private GameStatus gameStatus;

    Deck getDeck() {
        return deck;
    }

    void setDeck(Deck deck) {
        this.deck = deck;
    }

    int getCurrentRound() {
        return currentRound;
    }

    void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    int getNumberOfHumanPlayers() {
        return numberOfHumanPlayers;
    }

    void setNumberOfHumanPlayers(int numberOfHumanPlayers) {
        this.numberOfHumanPlayers = numberOfHumanPlayers;
    }

    int getNumberOfAllPlayers() {
        return numberOfAllPlayers;
    }

    void setNumberOfAllPlayers(int numberOfAllPlayers) {
        this.numberOfAllPlayers = numberOfAllPlayers;
    }

    Player getCurrentPlayer() {
        return currentPlayer;
    }

    void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    Player[] getPlayers() {
        return players;
    }

    void setPlayers(Player[] players) {
        this.players = players;
    }

    UserAttention getUserAttention() {
        return userAttention;
    }

    void setUserAttention(UserAttention userAttention) {
        this.userAttention = userAttention;
    }

    GameStatus getGameStatus() {
        return gameStatus;
    }

    void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    abstract void setUpGame() throws GameFlowException;
    abstract void dealCards();
    abstract void startTheGame() throws GameFlowException;
    abstract void currentSituation(Player player) throws GameFlowException;
    abstract void makeMove(Player player) throws GameFlowException;
    abstract void virtualPlayerMove(Player playerAI) throws GameFlowException;
    abstract void printResults() throws GameFlowException;
    abstract void printFinalScore() throws GameFlowException;

}
