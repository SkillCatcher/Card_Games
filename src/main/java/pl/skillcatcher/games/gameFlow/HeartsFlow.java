package pl.skillcatcher.games.gameFlow;

import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.PlayerStatus;
import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.games.ConsoleMessages;
import pl.skillcatcher.games.GameStatus;
import pl.skillcatcher.games.hearts.HeartsGame;
import pl.skillcatcher.interfaces.NameSetter;

public class HeartsFlow implements NameSetter, GameFlow {
    private HeartsGame heartsGame;

    HeartsFlow() {
        ConsoleMessages messages = new ConsoleMessages();
        UserInteraction userInteraction = new UserInteraction();
        System.out.println(messages.MENU_HEARTS_START);
        userInteraction.confirm();
        int numberOfPlayers = userInteraction.intInputWithCheck(messages.MENU_HEARTS_PLAYERS_NUM, 0, 4);
        heartsGame = new HeartsGame(numberOfPlayers, setNames(numberOfPlayers));
    }

    @Override
    public void play() throws GameFlowException {
        while (heartsGame.getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
            heartsGame.setUpGame();
            heartsGame.startTheGame();
            singleRound();
            heartsGame.printResults();
        }

        heartsGame.printFinalScore();
    }

    private void singleRound() throws GameFlowException {
        while (heartsGame.getGameStatus().equals(GameStatus.PLAYER_READY)) {
            singleMoveForAllPlayers();
            heartsGame.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
            heartsGame.moveResult();
        }
    }

    private void singleMoveForAllPlayers() throws GameFlowException {
        for (int i = 0; i < heartsGame.getPlayers().length; i++) {

            Player player = heartsGame.getPlayers()[(heartsGame.getCurrentPlayer().getId() + i) % 4];
            heartsGame.currentSituation(player);

            if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
                heartsGame.makeMove(player);
            } else {
                heartsGame.virtualPlayerMove(player);
            }
        }
    }
}
