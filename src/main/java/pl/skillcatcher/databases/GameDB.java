package pl.skillcatcher.databases;

import java.sql.*;

abstract public class GameDB {
    private final String DB_NAME;
    private final String CONNECTION_STRING;

    final String TABLE_CURRENT_GAME;
    final String[] COLUMN_PLAYERS;
    final String COLUMN_ROUND;

    Connection connection;

    public String[] getCOLUMN_PLAYERS() {
        return COLUMN_PLAYERS;
    }

    public GameDB(String DB_NAME, String TABLE_CURRENT_GAME, String[] columnPlayers) {
        this.DB_NAME = DB_NAME;
        this.CONNECTION_STRING = "jdbc:h2:/C:/Users/SkillCatcher/IdeaProjects/Card_Games/" + DB_NAME;
        this.TABLE_CURRENT_GAME = TABLE_CURRENT_GAME;
        this.COLUMN_PLAYERS = new String[columnPlayers.length];
        for (int i = 0; i < columnPlayers.length; i++) {
            String safeName = "\"" + removeQuotes(columnPlayers[i]) + "\"";
            this.COLUMN_PLAYERS[i] = safeName;
        }
        this.COLUMN_ROUND = "Round";
    }

    boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Can't connect to database - " + e.getMessage());
            return false;
        }
    }

    Statement createStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Can't create a statement - " + e.getMessage());
        }
        return null;
    }

    void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Can't close the database - " + e.getMessage());
        }
    }

    public abstract void setUpNewTable();
    public abstract void displayTable();

    String removeQuotes(String input) {
        StringBuilder changedInput = new StringBuilder(input);
        for (int i = 0; i < input.length(); i++) {
            if (getLetter(input, i).equals("\"")) {
                changedInput.setCharAt(i, ' ');
            }
        }
        return changedInput.toString();
    }

    private String getLetter(String input, int index) {
        return Character.toString(input.charAt(index));
    }

    String databaseTabulator(String columnName) {
        StringBuilder tab = new StringBuilder();
        for (int i = 0; i < ((columnName.length()-1)/4 + 1); i++) {
            tab.append("\t");
        }
        return tab.toString();
    }
}
