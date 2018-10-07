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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class HG_CurrentSituation_TestParameterized {
    private static String[] testNames = {"Test Player 1", "Test Player 2"};
    private static HeartsGame testHeartsGame = new HeartsGame(2, testNames);

    private UserInteraction mockedUA = mock(UserInteraction.class);
    private HeartsTable mockedHT = mock(HeartsTable.class);

    private static Player playerOne = testHeartsGame.getPlayers()[0];
    private static Player playerTwo = testHeartsGame.getPlayers()[1];
    private static Player playerThree = testHeartsGame.getPlayers()[2];
    private static Player playerFour = testHeartsGame.getPlayers()[3];
    private Card[] p1 = {new Card(3), new Card(13), new Card(23)};
    private Card[] p2 = {new Card(4), new Card(14), new Card(24)};
    private Card[] p3 = {new Card(5), new Card(15), new Card(25)};
    private Card[] p4 = {new Card(6), new Card(16), new Card(26)};

    private Player movingPlayer;
    private int confirmInvocations;
    private int displayCardsInvocations;
    private String message;

    public HG_CurrentSituation_TestParameterized(Player movingPlayer, int confirmInvocations,
                                                 int displayCardsInvocations, String message) {
        this.movingPlayer = movingPlayer;
        this.confirmInvocations = confirmInvocations;
        this.displayCardsInvocations = displayCardsInvocations;
        this.message = message;
    }

    @Before
    public void setup() {
        testHeartsGame.setUserInteraction(mockedUA);
        testHeartsGame.setHeartsTable(mockedHT);

        playerOne.getHand().setCards(Arrays.asList(p1));
        playerTwo.getHand().setCards(Arrays.asList(p2));
        playerThree.getHand().setCards(Arrays.asList(p3));
        playerFour.getHand().setCards(Arrays.asList(p4));
    }

    @Parameterized.Parameters
    public static Collection<Object> testConditions() {
        return Arrays.asList(new Object[][] {
                {playerOne, 2, 1, "TEST PLAYER 1 - IT'S YOUR TURN\n" +
                        "Other players - no peeking :)\n" +
                        "\n" +
                        "Cards in the game so far:\n" +
                        "Test Player 1 - your hand:\n" +
                        "1. Five of Diamonds\n" +
                        "2. Two of Spades\n" +
                        "3. Seven of Spades"},
                {playerTwo, 2, 1, "TEST PLAYER 2 - IT'S YOUR TURN\n" +
                        "Other players - no peeking :)\n" +
                        "\n" +
                        "Cards in the game so far:\n" +
                        "Test Player 2 - your hand:\n" +
                        "1. Three of Clubs\n" +
                        "2. Eight of Clubs\n" +
                        "3. Five of Hearts"},
                {playerThree, 1, 0, "AI #1 - IT'S YOUR TURN\n" +
                        "Other players - no peeking :)"},
                {playerFour, 1, 0, "AI #2 - IT'S YOUR TURN\n" +
                        "Other players - no peeking :)"}
        });
    }

    @Test
    public void should_Confirm_Twice_For_Users_And_Once_For_AIs() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_READY);

        testHeartsGame.currentSituation(movingPlayer);

        verify(mockedUA, times(confirmInvocations)).confirm();
    }

    @Test
    public void should_Display_Cards_Only_For_Users() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_READY);

        testHeartsGame.currentSituation(movingPlayer);

        verify(mockedHT, times(displayCardsInvocations)).displayCards();
    }

    @Test
    public void should_Correctly_Set_Up_New_Game_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_READY);

        testHeartsGame.currentSituation(movingPlayer);

        assertEquals(GameStatus.PLAYER_MOVING, testHeartsGame.getGameStatus());
    }

    @Test
    public void should_Correctly_Display_Info_To_The_Console() throws GameFlowException {
        final ByteArrayOutputStream changedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(changedOutput));
        testHeartsGame.setGameStatus(GameStatus.PLAYER_READY);

        testHeartsGame.currentSituation(movingPlayer);

        assertEquals(message, changedOutput.toString().trim().replace("\r",""));

        System.setOut(System.out);
    }

    @Test(expected = GameFlowException.class)
    public void currentSituation___should_Not_Run_With_Wrong_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.AFTER_SETUP);
        testHeartsGame.currentSituation(movingPlayer);
    }
}
