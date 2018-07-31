package pl.skillcatcher.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

abstract public class GameDB {
    final String DB_NAME;
    final String CONNECTION_STRING;

    final String TABLE_CURRENT_GAME;
    final String[] COLUMN_PLAYERS;
    final String COLUMN_ROUND;

    Connection connection;

    public GameDB(String DB_NAME, String TABLE_CURRENT_GAME, String[] COLUMN_PLAYERS) {
        this.DB_NAME = DB_NAME;
        this.CONNECTION_STRING = "jdbc:h2:/C:/Users/SkillCatcher/IdeaProjects/Card_Games/" + DB_NAME;
        this.TABLE_CURRENT_GAME = TABLE_CURRENT_GAME;
        this.COLUMN_PLAYERS = COLUMN_PLAYERS;
        this.COLUMN_ROUND = "Round";
    }

    public boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Can't connect to database - " + e.getMessage());
            return false;
        }
    }

    public Statement createStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Can't create a statement - " + e.getMessage());
        }
        return null;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Can't close the database - " + e.getMessage());
        }
    }
}
