package pl.skillcatcher.games;

import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Player;

abstract class Game {

    Deck deck;
    int numberOfHumanPlayers;
    int numberOfAllPlayers;
    Player currentPlayer;
    Player[] players;

    abstract void setCardValues();
    abstract void startTheGame();
    abstract void currentSituation(Player player);
    abstract void makeMove(Player player);
    abstract void AI_Move(Player playerAI);
    abstract void printResults();
    abstract void printFinalScore();

}
