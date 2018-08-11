package pl.skillcatcher.databases;

import pl.skillcatcher.features.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HeartsDB extends GameDB {

    public HeartsDB(String[] COLUMN_PLAYERS) {
        super("heartsResults.db", "HeartsGame", COLUMN_PLAYERS);
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
            System.out.println("Can't create a new Hearts table - " + e.getMessage());
            e.printStackTrace();
        }

        close();
    }

    public boolean saveCurrentRoundToTheTable(int round, Player[] players) {
        open();
        boolean result = false;

        try {
            String insert = "INSERT INTO " + TABLE_CURRENT_GAME + " ("
                    + databaseColumns(", ", "", false)
                    + ") VALUES (?, ?, ?, ?, ?)";

            PreparedStatement insertPlayersPointsAsValues = connection.prepareStatement(insert);
            insertPlayersPointsAsValues.setInt(1, round);
            for (int i = 0; i < COLUMN_PLAYERS.length; i++) {
                insertPlayersPointsAsValues.setInt(i+2, players[i].getPoints());
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
            ResultSet roundsPlayed = statement.executeQuery("SELECT * FROM " + TABLE_CURRENT_GAME);
            System.out.println("\n" + databaseColumns(":\t", ":", true));

            while (roundsPlayed.next()) {
                System.out.println(roundsPlayed.getInt(COLUMN_ROUND)
                    + "\t\t" + roundsPlayed.getInt(shave(COLUMN_PLAYERS[0]))
                    + databaseTabulator(shave(COLUMN_PLAYERS[0])) + roundsPlayed.getInt(shave(COLUMN_PLAYERS[1]))
                    + databaseTabulator(shave(COLUMN_PLAYERS[1])) + roundsPlayed.getInt(shave(COLUMN_PLAYERS[2]))
                    + databaseTabulator(shave(COLUMN_PLAYERS[2])) + roundsPlayed.getInt(shave(COLUMN_PLAYERS[3]))
                );
            }

            roundsPlayed.close();
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
        columns.append(last);

        return columns;
    }
}
