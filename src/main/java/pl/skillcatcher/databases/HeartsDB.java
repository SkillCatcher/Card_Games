package pl.skillcatcher.databases;

public class HeartsDB extends GameDB {
    public final String COLUMN_PLAYER_1;
    public final String COLUMN_PLAYER_2;
    public final String COLUMN_PLAYER_3;
    public final String COLUMN_PLAYER_4;

    public HeartsDB(String[] playersNames) {
        super("heartsResults.db", "HeartsGame", playersNames);
        this.COLUMN_PLAYER_1 = playersNames[0];
        this.COLUMN_PLAYER_2 = playersNames[1];
        this.COLUMN_PLAYER_3 = playersNames[2];
        this.COLUMN_PLAYER_4 = playersNames[3];
    }
}
