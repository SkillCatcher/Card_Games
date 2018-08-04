package pl.skillcatcher.games;

import pl.skillcatcher.cards.Player;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.interfaces.Confirmable;
import pl.skillcatcher.interfaces.CorrectIntInputCheck;
import pl.skillcatcher.interfaces.NameSetter;

public class MainMenu implements CorrectIntInputCheck, Confirmable, NameSetter {

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
                confirm();
                int numberOfPlayers = intInputWithCheck("Please choose a number of players " +
                        "(between 1 and 7):", 1, 7);
                int numberOfRounds = intInputWithCheck("Please choose a number of rounds you " +
                        " want to play (between 1 and 100):", 1, 100);

                BlackJack blackJack = new BlackJack(numberOfPlayers, numberOfRounds, setNames(numberOfPlayers));

                while (blackJack.getBjStatus().equals(BlackJackStatus.BEFORE_SETUP)) {
                    blackJack.setUpGame();

                    while (blackJack.getBjStatus().equals(BlackJackStatus.AFTER_SETUP)) {
                        blackJack.startTheGame();

                        if (blackJack.getBjStatus().equals(BlackJackStatus.PLAYER_READY)) {
                            for (Player player : blackJack.getNotFinishedPlayers()) {
                                blackJack.currentSituation(player);
                            }
                            blackJack.setBjStatus(BlackJackStatus.AFTER_SETUP);
                        } else if (!(blackJack.getBjStatus().equals(BlackJackStatus.ALL_PLAYERS_DONE))) {
                            throw new GameFlowException("Can't make a move");
                        }
                    }

                    while (blackJack.getBjStatus().equals(BlackJackStatus.ALL_PLAYERS_DONE)) {
                        blackJack.virtualPlayerMove(blackJack.getDealer());
                    }

                    if (blackJack.getBjStatus().equals(BlackJackStatus.ROUND_DONE)) {
                        blackJack.printResults();
                    } else {
                        throw new GameFlowException("Can't print results from current round");
                    }

                    if (blackJack.getBjStatus().equals(BlackJackStatus.GAME_DONE)) {
                        blackJack.printFinalScore();
                    } else if (blackJack.getBjStatus().equals(BlackJackStatus.BEFORE_SETUP)) {
                        confirm();
                    } else {
                        throw new GameFlowException("Can't play another round or print final results");
                    }
                }
                break;
            case 2:
                System.out.println("Let's play Hearts!");
                confirm();
                Hearts hearts = new Hearts();
                hearts.startTheGame();
                break;
            default:
                break;
        }
    }
}
