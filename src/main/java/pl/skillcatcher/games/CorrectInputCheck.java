package pl.skillcatcher.games;

import java.util.Scanner;

public interface CorrectInputCheck {
    default int inputWithCheck(String message, int min, int max) {
        System.out.println("\n" + message + "\n");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();

            if (choice > max || choice < min) {
                System.out.println("\nIncorrect number - please choose a number between "
                        + min + " and " + max + ":\n");
                return inputWithCheck(message, min, max);
            } else {
                return choice;
            }

        } else {
            System.out.println("\nIncorrect input - please choose a NUMBER between "
                    + min + " and " + max + ":\n");
            return inputWithCheck(message, min, max);
        }
    }
}
