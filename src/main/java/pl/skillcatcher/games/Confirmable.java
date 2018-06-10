package pl.skillcatcher.games;

import java.util.Scanner;

public interface Confirmable {

    default void confirm() {
        System.out.println("To continue, press Enter...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
