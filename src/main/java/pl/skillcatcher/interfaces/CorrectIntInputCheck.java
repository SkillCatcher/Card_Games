package pl.skillcatcher.interfaces;

import java.util.Scanner;

public interface CorrectIntInputCheck {
    default int intInputWithCheck(String message, int min, int max) {
        System.out.println("\n" + message);
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();

            if (choice > max || choice < min) {
                System.out.println("\nIncorrect number - please choose a number between "
                        + min + " and " + max + ":");
                return intInputWithCheck(message, min, max);
            } else {
                return choice;
            }

        } else {
            System.out.println("\nIncorrect input - please choose a NUMBER between "
                    + min + " and " + max + ":");
            return intInputWithCheck(message, min, max);
        }
    }
}
