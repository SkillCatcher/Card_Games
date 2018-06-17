package pl.skillcatcher.games;

import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Player;

abstract class Game {

    Deck deck;
    int numberOfPlayers;
    Player currentPlayer;

    abstract void setCardValues();
    abstract void startTheGame();
    abstract void currentSituation(Player player);
    abstract void makeMove(Player player);
    abstract void AI_Move();
    abstract void printResults();
    abstract void printFinalScore();

}
