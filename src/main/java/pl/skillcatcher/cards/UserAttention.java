package pl.skillcatcher.cards;

import java.util.Scanner;

public class UserAttention {

    public UserAttention() {
    }

    public void confirm() {
        System.out.println("To continue, press Enter (or write anything in the console - it doesn't matter)");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
