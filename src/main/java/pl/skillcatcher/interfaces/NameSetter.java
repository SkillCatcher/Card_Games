package pl.skillcatcher.interfaces;

import java.util.Scanner;

public interface NameSetter {
    default String[] setNames(int numberOfHumanPlayers) {
        Scanner scanner = new Scanner(System.in);
        String[] names = new String[numberOfHumanPlayers];
        for (int i = 0; i < numberOfHumanPlayers; i++) {
            System.out.println("Player " + (i+1) + " - name:\n");
            names[i] = scanner.nextLine();
            boolean noName = true;
            for (int j = 0; j < names[i].length(); j++) {
                if (!Character.toString(names[i].charAt(j)).equals(" ")) {
                    noName = false;
                    break;
                }
            }
            if (noName || names[i].equals("")) {
                names[i] = "Unknown";
            }
        }
        return names;
    }
}
