package pl.skillcatcher.application;

import pl.skillcatcher.games.Confirmable;
import pl.skillcatcher.games.CorrectIntInputCheck;
import pl.skillcatcher.games.MainMenu;

public class GameRunner implements Confirmable, CorrectIntInputCheck {
    public static void main(String[] args) {
        MainMenu cardGame = new MainMenu();
    }
}

