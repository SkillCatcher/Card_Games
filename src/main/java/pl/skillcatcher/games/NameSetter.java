package pl.skillcatcher.games;

import java.util.Scanner;

public interface NameSetter {
    default String[] setNames(int numberOfHumanPlayers) {
        Scanner scanner = new Scanner(System.in);
        String[] names = new String[numberOfHumanPlayers];
        for (int i = 0; i < numberOfHumanPlayers; i++) {
            System.out.println("Player " + (i+1) + " - name:\n");
            names[i] = scanner.nextLine();
            if (names[i].equals("")) {
                names[i] = "Unknown";
            }
        }
        return names;
    }
}
