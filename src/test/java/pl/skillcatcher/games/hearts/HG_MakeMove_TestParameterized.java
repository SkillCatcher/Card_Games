package pl.skillcatcher.games.hearts;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.features.Card;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.games.GameStatus;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class HG_MakeMove_TestParameterized {
    private static String[] testNames = {"Test Player 1", "Test Player 2", "Test Player 3", "Test Player 4"};
    private static HeartsGame testHeartsGame = new HeartsGame(4, testNames);

    private UserInteraction mockedUA = mock(UserInteraction.class);
    private HeartsTable mockedHT = mock(HeartsTable.class);

    private static Player playerOne = testHeartsGame.getPlayers()[0];
    private static Player playerTwo = testHeartsGame.getPlayers()[1];
    private static Player playerThree = testHeartsGame.getPlayers()[2];
    private static Player playerFour = testHeartsGame.getPlayers()[3];

    private Player movingPlayer;
    private int cardNumberFromList;
    private int expectedConfirms;
    private boolean canBePlayed_TestValue;
    private boolean colourRule_TestValue;
    private boolean heartsRule_TestValue;
    private boolean firstRoundRule_TestValue;
    private String textToConsole;

    public HG_MakeMove_TestParameterized(Player movingPlayer, int cardNumberFromList, int expectedConfirms,
                                         boolean colourRule_TestValue, boolean heartsRule_TestValue,
                                         boolean firstRoundRule_TestValue, String textToConsole) {
        this.movingPlayer = movingPlayer;
        this.cardNumberFromList = cardNumberFromList;
        this.expectedConfirms = expectedConfirms;
        this.canBePlayed_TestValue = colourRule_TestValue && heartsRule_TestValue && firstRoundRule_TestValue;
        this.colourRule_TestValue = colourRule_TestValue;
        this.heartsRule_TestValue = heartsRule_TestValue;
        this.firstRoundRule_TestValue = firstRoundRule_TestValue;
        this.textToConsole = textToConsole;
    }

    @Before
    public void setup() {
        testHeartsGame.setUserInteraction(mockedUA);
        testHeartsGame.setHeartsTable(mockedHT);

        int[] cardIds = {2, 6, 18, 10, 22, 14, 26, 30};

        for (int i = 0; i < cardIds.length; i++) {
            testHeartsGame.getPlayers()[i % testHeartsGame.getPlayers().length].getCards().add(new Card(cardIds[i]));
        }

        when(mockedUA.intInputWithCheck("Pick a card: ", 1, 2)).thenReturn(cardNumberFromList);
        when(mockedHT.canBePlayed(movingPlayer.getHand(),
                movingPlayer.getCard(cardNumberFromList-1), movingPlayer)).thenReturn(canBePlayed_TestValue);
        when(mockedHT.canBePlayed_ColourRule(movingPlayer.getHand(),
                movingPlayer.getCard(cardNumberFromList-1), movingPlayer)).thenReturn(colourRule_TestValue);
        when(mockedHT.canBePlayed_HeartsRule(movingPlayer.getHand(),
                movingPlayer.getCard(cardNumberFromList-1))).thenReturn(heartsRule_TestValue);
        when(mockedHT.canBePlayed_FirstRoundRule(movingPlayer.getHand(),
                movingPlayer.getCard(cardNumberFromList-1))).thenReturn(firstRoundRule_TestValue);
    }

    @Parameterized.Parameters
    public static Collection<Object> testConditions() {
        return Arrays.asList(new Object[][] {
                {playerOne, 1, 1, false, false, false, ""},
                {playerOne, 2, 1, true, false, false, ""},
                {playerTwo, 1, 1, false, true, false, ""},
                {playerTwo, 2, 1, false, false, true, ""},
                {playerThree, 1, 1, false, true, true, ""},
                {playerThree, 2, 1, true, false, true, ""},
                {playerFour, 1, 1, true, true, false, ""},
                {playerFour, 2, 0, true, true, true, ""}
        });
    }

    @Test
    public void make_Move___should_Print_Text_To_The_Console() {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);

        try {
            testHeartsGame.makeMove(movingPlayer);
        } catch (GameFlowException e) {
            e.printStackTrace();
        }



    }

    @Test
    public void make_Move___should_Confirm_Or_Not() {

    }

}
