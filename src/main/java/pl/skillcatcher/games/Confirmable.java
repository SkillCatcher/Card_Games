package pl.skillcatcher.games;

import java.util.Scanner;

public interface Confirmable {

    default void confirm() {
        System.out.println("To continue, press Enter (or write anything in the console - it doesn't matter)");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
