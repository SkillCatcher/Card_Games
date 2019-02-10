package pl.skillcatcher.features;

import pl.skillcatcher.games.ConsoleMessages;

import java.util.Scanner;

public class UserInteraction {
    private ConsoleMessages messages = new ConsoleMessages();

    public UserInteraction() {
    }

    public void confirm() {
        System.out.println(messages.UI_CONFIRM);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public int intInputWithCheck(String message, int min, int max) {
        boolean inputIsValid = false;
        int choice = -2147483647;

        while (!inputIsValid) {
            System.out.println("\n" + message);
            Scanner scanner = new Scanner(System.in);

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                inputIsValid = isInputInGivenRange(choice, min, max);
            } else {
                System.out.println(String.format(messages.UI_NOT_NUMBER, min, max));
            }
        }

        return choice;
    }

    private boolean isInputInGivenRange(int inputNumber, int min, int max) {
        if (inputNumber > max || inputNumber < min) {
            System.out.println(String.format(messages.UI_WRONG_NUMBER, min, max));
            return false;
        } else {
            return true;
        }
    }
}
