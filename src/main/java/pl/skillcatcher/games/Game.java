package pl.skillcatcher.games;

public interface Game {
    void setCardValues();
    void startTheGame();
    void currentSituation();
    void makeMove();
    void AI_Move();
    void printResults();
    void printFinalScore();
    void confirm();
}
