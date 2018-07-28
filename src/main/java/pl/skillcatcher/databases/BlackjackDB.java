package pl.skillcatcher.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BlackjackDB {
    public final String DB_NAME = "blackjackResults.db";
    public final String CONNECTION_STRING = "jdbc:h2:/C:/Users/SkillCatcher/IdeaProjects/Card_Games/" + DB_NAME;

    public final String TABLE_CURRENT_GAME = "BlackJackGame";

    public final String COLUMN_ROUND = "Round";
    public final String COLUMN_DEALER = "Dealer";
    public final String[] COLUMN_PLAYERS;

    public BlackjackDB(String[] COLUMN_PLAYERS) {
        this.COLUMN_PLAYERS = COLUMN_PLAYERS;
    }

    private Connection connection;

    public boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Can't connect to database - " + e.getMessage());
            return false;
        }
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
