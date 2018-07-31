package pl.skillcatcher.databases;

import pl.skillcatcher.cards.Player;

import java.sql.*;

public class BlackjackDB extends GameDB {

    public final String COLUMN_DEALER = "Dealer";

    public BlackjackDB(String[] COLUMN_PLAYERS) {
        super("blackjackResults.db", "BlackJackGame", COLUMN_PLAYERS);
    }

    public void createNewTable() {
        open();
        try {

            Statement statement = createStatement();

            statement.execute("DROP TABLE IF EXISTS " + TABLE_CURRENT_GAME);

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT_GAME + " ("
                    + databaseColumns(" int, ") + " int)");

            statement.close();
        } catch (SQLException e) {
            System.out.println("Error - " + e.getMessage());
            e.printStackTrace();
        }
        close();
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
