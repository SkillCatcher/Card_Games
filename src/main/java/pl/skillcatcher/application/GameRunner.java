package pl.skillcatcher.application;

import pl.skillcatcher.games.Confirmable;
import pl.skillcatcher.games.CorrectIntInputCheck;
import pl.skillcatcher.games.Hearts;
import pl.skillcatcher.games.MainMenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GameRunner implements Confirmable, CorrectIntInputCheck {
    public static void main(String[] args) {
        //MainMenu cardGame = new MainMenu();
        Hearts hearts = new Hearts();
        hearts.setUpGame();
    }
}

