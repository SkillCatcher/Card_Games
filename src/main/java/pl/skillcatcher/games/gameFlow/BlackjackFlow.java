package pl.skillcatcher.games.gameFlow;

import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.games.ConsoleMessages;
import pl.skillcatcher.games.GameStatus;
import pl.skillcatcher.games.blackjack.BlackJack;
import pl.skillcatcher.interfaces.NameSetter;

public class BlackjackFlow implements NameSetter, GameFlow {
    private BlackJack blackJack;

    BlackjackFlow() {
        ConsoleMessages messages = new ConsoleMessages();
        UserInteraction userInteraction = new UserInteraction();
        System.out.println(messages.MENU_BLACKJACK_START);
        userInteraction.confirm();
        int numberOfPlayers = userInteraction.intInputWithCheck(messages.MENU_BLACKJACK_PLAYERS_NUM, 1, 7);
        int numberOfRounds = userInteraction.intInputWithCheck(messages.MENU_BLACKJACK_ROUNDS_NUM, 1, 100);
        this.blackJack = new BlackJack(numberOfPlayers, numberOfRounds, setNames(numberOfPlayers));
    }

    @Override
    public void play() throws GameFlowException {
        while (blackJack.getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
            blackJack.setUpGame();
            playersMoves();
            dealersMove();
            blackJack.printResults();
        }
        blackJack.printFinalScore();
    }

    private void playersMoves() throws GameFlowException {
        while (blackJack.getGameStatus().equals(GameStatus.AFTER_SETUP)) {
            blackJack.startTheGame();
            if (blackJack.getGameStatus().equals(GameStatus.PLAYER_READY)) {
                singleRound();
            }
        }
    }

    private void singleRound() throws GameFlowException {
        for (Player player : blackJack.getPlayersInGame().getNotFinishedPlayers()) {
            blackJack.currentSituation(player);
            if (blackJack.getGameStatus().equals(GameStatus.PLAYER_MOVING)) blackJack.makeMove(player);
        }
        blackJack.setGameStatus(GameStatus.AFTER_SETUP);
    }

    private void dealersMove() throws GameFlowException {
        while (blackJack.getGameStatus().equals(GameStatus.ALL_PLAYERS_DONE)) {
            blackJack.virtualPlayerMove(blackJack.getDealer());
        }
    }
}
