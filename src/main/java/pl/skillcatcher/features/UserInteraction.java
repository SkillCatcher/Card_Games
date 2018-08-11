package pl.skillcatcher.features;

import java.util.Scanner;

public class UserInteraction {

    public UserInteraction() {
    }

    public void confirm() {
        System.out.println("To continue, press Enter (or write anything in the console - it doesn't matter)");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public int intInputWithCheck(String message, int min, int max) {
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
