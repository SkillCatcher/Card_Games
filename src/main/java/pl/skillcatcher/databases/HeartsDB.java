package pl.skillcatcher.databases;

import pl.skillcatcher.cards.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HeartsDB extends GameDB {

    public HeartsDB(String[] playersNames) {
        super("heartsResults.db", "HeartsGame", playersNames);
    }

    public void createNewTable() {
        open();
        Statement statement = createStatement();

        try {
            statement.execute("DROP TABLE IF EXISTS " + TABLE_CURRENT_GAME);

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT_GAME + " ("
                    + databaseColumns(" int, ", " int)"));

            statement.close();
        } catch (SQLException e) {
            System.out.println("Can't create a new table - " + e.getMessage());
            e.printStackTrace();
        }

        close();
    }

    public boolean saveCurrentRoundToTheTable(int round, Player[] players) {
        open();
        boolean result = false;

        try {
            String insert = "INSERT INTO " + TABLE_CURRENT_GAME + " (" + databaseColumns(", ", "")
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
        Statement statement = createStatement();
        try {
            ResultSet roundsPlayed = statement.executeQuery("SELECT * FROM " + TABLE_CURRENT_GAME);
            System.out.println("\n" + databaseColumns(":\t", ":"));

            while (roundsPlayed.next()) {
                System.out.println(roundsPlayed.getInt(COLUMN_ROUND)
                    + "\t\t" + roundsPlayed.getInt(COLUMN_PLAYERS[0])
                    + "\t\t" + roundsPlayed.getInt(COLUMN_PLAYERS[1])
                    + "\t\t" + roundsPlayed.getInt(COLUMN_PLAYERS[2])
                    + "\t\t" + roundsPlayed.getInt(COLUMN_PLAYERS[3])
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

    private StringBuilder databaseColumns(String inBetween, String last) {
        StringBuilder columns = new StringBuilder(COLUMN_ROUND);
        for (String string : COLUMN_PLAYERS) {
            columns.append(inBetween).append(string);
        }
        columns.append(last);

        return columns;
    }


}
