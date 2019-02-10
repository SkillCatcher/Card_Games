package pl.skillcatcher.games.gameFlow;

import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.games.ConsoleMessages;
import pl.skillcatcher.interfaces.NameSetter;

public class MainMenu implements NameSetter {
    private ConsoleMessages messages = new ConsoleMessages();

    public MainMenu() {
        System.out.println(messages.MENU_WELCOME_MESSAGE);
        try {
            start();
        } catch (GameFlowException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void start() throws GameFlowException {
        int gameChoice = new UserInteraction().intInputWithCheck(messages.MENU_GAME_CHOICE, 0, 2);
        switch (gameChoice) {
            case 0:
                System.out.println(messages.MENU_QUIT);
                break;
            case 1:
                BlackjackFlow blackjack = new BlackjackFlow();
                blackjack.play();
                break;
            case 2:
                HeartsFlow hearts = new HeartsFlow();
                hearts.play();
                break;
            default:
                System.out.println(messages.MENU_TO_BE_CONTINUED);
                break;
        }
    }
}
