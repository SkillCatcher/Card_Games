package pl.skillcatcher.databases;

import pl.skillcatcher.cards.Player;

import java.sql.*;

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

    public Statement createStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Can't create a statement - " + e.getMessage());
        }
        return null;
    }

    public void createNewTable() {
        try {
            open();
            Statement statement = createStatement();

            statement.execute("DROP TABLE IF EXISTS " + TABLE_CURRENT_GAME);

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT_GAME + " ("
                    + databaseColumns(" int, ") + " int)");

            statement.close();
            close();
        } catch (SQLException e) {
            System.out.println("Error - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean saveCurrentRoundIntoTable(int round, Player[] players, Player dealer) {
        open();
        boolean result = false;

        try {
            String insert = "INSERT INTO " + TABLE_CURRENT_GAME + " (" + databaseColumns(", ") +
                    ") VALUES (" + databaseValues() + ")";

            PreparedStatement insertPlayersPointsAsValues = connection.prepareStatement(insert);
            insertPlayersPointsAsValues.setInt(1, round);
            for (int i = 0; i < COLUMN_PLAYERS.length + 1; i++) {
                if (i < players.length) {
                    insertPlayersPointsAsValues.setInt(i+2, players[i].getPoints());
                } else {
                    insertPlayersPointsAsValues.
                            setInt(i+2, dealer.getPoints()/COLUMN_PLAYERS.length);
                }
            }

            int addedRecords = insertPlayersPointsAsValues.executeUpdate();

            if (addedRecords == 1) {
                result = true;
            }

            close();
        } catch (SQLException e) {
            System.out.println("Can't insert data - " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public void displayTable() {
        open();
        try {
            Statement statement = createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM " + TABLE_CURRENT_GAME);
            System.out.println("\n" + databaseColumns(":\t") + ":");

            while (results.next()) {
                for (int i = 0; i < COLUMN_PLAYERS.length + 1; i++) {
                    System.out.print(results.getInt(i+1));
                    System.out.print("\t\t");
                }
                System.out.print(results.getInt(COLUMN_DEALER));
                System.out.println();
            }

            results.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Can't select the data - " + e.getMessage());
            e.printStackTrace();
        }

        close();
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

    private StringBuilder databaseColumns(String inBetween) {
        StringBuilder columns = new StringBuilder(COLUMN_ROUND);
        for (String string : COLUMN_PLAYERS) {
            columns.append(inBetween).append(string);
        }
        columns.append(inBetween).append(COLUMN_DEALER);

        return columns;
    }

    private String databaseValues() {
        StringBuilder values = new StringBuilder("?, ");
        for (String string : COLUMN_PLAYERS) {
            values.append("?, ");
        }
        values.append("?");

        return values.toString();
    }
}
