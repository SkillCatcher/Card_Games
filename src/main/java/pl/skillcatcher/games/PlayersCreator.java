package pl.skillcatcher.games;

import pl.skillcatcher.cards.Player;
import pl.skillcatcher.cards.PlayerStatus;

import java.util.Scanner;

public interface PlayersCreator {
    default void createPlayers(int numberOfHumanPlayers, int numberOfAllPlayers, Player[] players) {
        for (int i = 0; i < numberOfAllPlayers; i++) {
            if (i < numberOfHumanPlayers) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Player " + (i+1) + " - name:\n");
                String name = scanner.nextLine();
                players[i] = new Player(name, i);
            } else {
                players[i] = new Player("A.I. #" + (i+1-numberOfHumanPlayers), i, PlayerStatus.AI);
            }
        }
    }
}
