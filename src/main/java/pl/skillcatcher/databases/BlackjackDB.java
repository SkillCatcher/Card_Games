package pl.skillcatcher.databases;

import pl.skillcatcher.features.Player;

import java.sql.*;

public class BlackjackDB extends GameDB {

    private final String COLUMN_DEALER = "Dealer";

    public BlackjackDB(String[] COLUMN_PLAYERS) {
        super("blackjackResults.db", "BlackJackGame", COLUMN_PLAYERS);
    }

    public void setUpNewTable() {
        open();
        Statement statement = createStatement();

        try {
            statement.execute("DROP TABLE IF EXISTS " + TABLE_CURRENT_GAME);

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT_GAME + " ("
                    + databaseColumns(" int, ", " int)", false));

            statement.close();
        } catch (SQLException e) {
            System.out.println("Can't create a new Black Jack table - " + e.getMessage());
            e.printStackTrace();
        }

        close();
    }

    public boolean saveCurrentRoundIntoTable(int round, Player[] players, Player dealer) {
        open();
        boolean result = false;

        try {
            String insert = "INSERT INTO " + TABLE_CURRENT_GAME + " (" +
                    databaseColumns(", ", "", false) + ") VALUES (" +
                    databaseValues() + ")";

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

        } catch (SQLException e) {
            System.out.println("Can't insert data - " + e.getMessage());
            e.printStackTrace();
        }

        close();
        return result;
    }

    public void displayTable() {
        open();
        try {
            Statement statement = createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM " + TABLE_CURRENT_GAME);
            System.out.println("\n" + databaseColumns(":\t", ":", true));

            while (results.next()) {
                System.out.print(results.getInt(1));
                System.out.print(databaseTabulator(COLUMN_ROUND));
                for (int i = 0; i < COLUMN_PLAYERS.length; i++) {
                    System.out.print(results.getInt(i+2));
                    System.out.print(databaseTabulator(COLUMN_PLAYERS[i]));
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

    private StringBuilder databaseColumns(String inBetween, String last, boolean areQuotesRemoved) {
        StringBuilder columns = new StringBuilder(COLUMN_ROUND);
        for (String string : COLUMN_PLAYERS) {
            if (areQuotesRemoved) {
                string = removeQuotes(string).trim();
            }
            columns.append(inBetween).append(string);
        }
        columns.append(inBetween).append(COLUMN_DEALER).append(last);

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
