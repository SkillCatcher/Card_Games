package pl.skillcatcher.games;

import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.PlayerStatus;
import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.games.blackjack.BlackJack;
import pl.skillcatcher.games.hearts.HeartsGame;
import pl.skillcatcher.interfaces.NameSetter;

public class MainMenu implements NameSetter {
    private UserInteraction userInteraction = new UserInteraction();
    private int numberOfPlayers;

    public MainMenu() {
        System.out.println("Welcome! Today, all of these games are available:"
                + "\n\t1. BlackJack"
                + "\n\t2. Hearts"
                + "\nMore games in progress :)");
        try {
            start(userInteraction.intInputWithCheck("To choose your game, press it's number from the list " +
                            "(or you can press 0 to quit)", 0, 2));
        } catch (GameFlowException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void start(int gameChoice) throws GameFlowException {
        switch (gameChoice) {
            case 0:
                System.out.println("Have a nice day :)");
                break;
            case 1:
                System.out.println("Let's play BlackJack!");
                userInteraction.confirm();
                numberOfPlayers = userInteraction.intInputWithCheck("Please choose a number of players " +
                        "(between 1 and 7):", 1, 7);
                int numberOfRounds = userInteraction.intInputWithCheck("Please choose a number of rounds you " +
                        " want to play (between 1 and 100):", 1, 100);

                BlackJack blackJack = new BlackJack(numberOfPlayers, numberOfRounds, setNames(numberOfPlayers));

                while (blackJack.getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
                    blackJack.setUpGame();

                    while (blackJack.getGameStatus().equals(GameStatus.AFTER_SETUP)) {
                        blackJack.startTheGame();

                        if (blackJack.getGameStatus().equals(GameStatus.PLAYER_READY)) {
                            for (Player player : blackJack.getPlayersInGame().getNotFinishedPlayers()) {
                                blackJack.currentSituation(player);
                                if (blackJack.getGameStatus().equals(GameStatus.PLAYER_MOVING)) {
                                    blackJack.makeMove(player);
                                }
                            }
                            blackJack.setGameStatus(GameStatus.AFTER_SETUP);
                        } else if (!(blackJack.getGameStatus().equals(GameStatus.ALL_PLAYERS_DONE))) {
                            throw new GameFlowException("Can't make a move");
                        }
                    }

                    while (blackJack.getGameStatus().equals(GameStatus.ALL_PLAYERS_DONE)) {
                        blackJack.virtualPlayerMove(blackJack.getDealer());
                    }

                    if (blackJack.getGameStatus().equals(GameStatus.ROUND_DONE)) {
                        blackJack.printResults();
                    } else {
                        throw new GameFlowException("Can't play another round or print results from current one");
                    }
                }

                if (blackJack.getGameStatus().equals(GameStatus.GAME_DONE)) {
                    blackJack.printFinalScore();
                } else {
                    throw new GameFlowException("Can't print the final score");
                }

                break;
            case 2:
                System.out.println("Let's play Hearts!");
                userInteraction.confirm();
                numberOfPlayers = userInteraction.intInputWithCheck("Please choose the number of HUMAN players " +
                        "- between 0 (if you just want to watch and press enter) and 4 (all human players):",
                        0, 4);

                HeartsGame heartsGame = new HeartsGame(numberOfPlayers, setNames(numberOfPlayers));

                while (heartsGame.getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
                    heartsGame.setUpGame();

                    if (heartsGame.getGameStatus().equals(GameStatus.AFTER_SETUP)) {
                        heartsGame.startTheGame();
                    } else {
                        throw new GameFlowException("Can't start the game");
                    }

                    while (heartsGame.getGameStatus().equals(GameStatus.PLAYER_READY)) {

                        for (int i = 0; i < heartsGame.getPlayers().length; i++) {

                            Player player = heartsGame.getPlayers()[(heartsGame.getCurrentPlayer().getId() + i) % 4];

                            if (heartsGame.getGameStatus().equals(GameStatus.PLAYER_READY)) {
                                heartsGame.currentSituation(player);
                            } else {
                                throw new GameFlowException("Can't display current situation");
                            }

                            if (heartsGame.getGameStatus().equals(GameStatus.PLAYER_MOVING)) {
                                if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
                                    heartsGame.makeMove(player);
                                } else {
                                    heartsGame.virtualPlayerMove(player);
                                }
                            } else {
                                throw new GameFlowException("Can't make a move");
                            }
                        }

                        heartsGame.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
                        heartsGame.moveResult();
                    }

                    if (heartsGame.getGameStatus().equals(GameStatus.ROUND_DONE)) {
                        heartsGame.printResults();
                    } else {
                        throw new GameFlowException("Can't play another round or print results from current one");
                    }
                }

                if (heartsGame.getGameStatus().equals(GameStatus.GAME_DONE)) {
                    heartsGame.printFinalScore();
                } else {
                    throw new GameFlowException("Can't print the final score");
                }

                break;
            default:
                break;
        }
    }
}
