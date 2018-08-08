package pl.skillcatcher.games;

import pl.skillcatcher.cards.Player;
import pl.skillcatcher.cards.PlayerStatus;
import pl.skillcatcher.cards.UserAttention;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.interfaces.CorrectIntInputCheck;
import pl.skillcatcher.interfaces.NameSetter;

public class MainMenu implements CorrectIntInputCheck, NameSetter {
    private UserAttention userAttention = new UserAttention();
    private int numberOfPlayers;

    public MainMenu() {
        System.out.println("Welcome! Today, all of these games are available:"
                + "\n\t1. BlackJack"
                + "\n\t2. Hearts"
                + "\nMore games in progress :)");
        try {
            start(intInputWithCheck("To choose your game, press it's number from the list " +
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
                userAttention.confirm();
                numberOfPlayers = intInputWithCheck("Please choose a number of players " +
                        "(between 1 and 7):", 1, 7);
                int numberOfRounds = intInputWithCheck("Please choose a number of rounds you " +
                        " want to play (between 1 and 100):", 1, 100);

                BlackJack blackJack = new BlackJack(numberOfPlayers, numberOfRounds, setNames(numberOfPlayers));

                while (blackJack.getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
                    blackJack.setUpGame();

                    while (blackJack.getGameStatus().equals(GameStatus.AFTER_SETUP)) {
                        blackJack.startTheGame();

                        if (blackJack.getGameStatus().equals(GameStatus.PLAYER_READY)) {
                            for (Player player : blackJack.getNotFinishedPlayers()) {
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
                userAttention.confirm();
                numberOfPlayers = intInputWithCheck("Please choose the number of HUMAN players " +
                        "- between 0 (if you just want to watch and press enter) and 4 (all human players):",
                        0, 4);

                Hearts hearts = new Hearts(numberOfPlayers, setNames(numberOfPlayers));

                while (hearts.getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
                    hearts.setUpGame();

                    if (hearts.getGameStatus().equals(GameStatus.AFTER_SETUP)) {
                        hearts.startTheGame();
                    } else {
                        throw new GameFlowException("Can't start the game");
                    }

                    while (hearts.getGameStatus().equals(GameStatus.PLAYER_READY)) {

                        for (int i = 0; i < hearts.getPlayers().length; i++) {

                            Player player = hearts.getPlayers()[(hearts.getCurrentPlayer().getId() + i) % 4];

                            if (hearts.getGameStatus().equals(GameStatus.PLAYER_READY)) {
                                hearts.currentSituation(player);
                            } else {
                                throw new GameFlowException("Can't display current situation");
                            }

                            if (hearts.getGameStatus().equals(GameStatus.PLAYER_MOVING)) {
                                if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
                                    hearts.makeMove(player);
                                } else {
                                    hearts.virtualPlayerMove(player);
                                }
                            } else {
                                throw new GameFlowException("Can't make a move");
                            }
                        }

                        hearts.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
                        hearts.moveResult();
                    }

                    if (hearts.getGameStatus().equals(GameStatus.ROUND_DONE)) {
                        hearts.printResults();
                    } else {
                        throw new GameFlowException("Can't play another round or print results from current one");
                    }
                }

                if (hearts.getGameStatus().equals(GameStatus.GAME_DONE)) {
                    hearts.printFinalScore();
                } else {
                    throw new GameFlowException("Can't print the final score");
                }

                break;
            default:
                break;
        }
    }
}
