package pl.skillcatcher.games.hearts;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.skillcatcher.asserts.PlayerAssert;
import pl.skillcatcher.features.*;
import pl.skillcatcher.databases.HeartsDB;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.games.GameStatus;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HeartsGameTest {
    private final ByteArrayOutputStream changedOutput = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private String[] testNames = {"Test Player 1", "Test Player 2"};
    private HeartsGame testHeartsGame = new HeartsGame(2, testNames);
    private Player playerOne = testHeartsGame.getPlayers()[0];
    private Player playerTwo = testHeartsGame.getPlayers()[1];
    private Player playerThree = testHeartsGame.getPlayers()[2];
    private Player playerFour = testHeartsGame.getPlayers()[3];

    private HeartsDB mockedDB = mock(HeartsDB.class);
    private UserInteraction mockedUA = mock(UserInteraction.class);
    private CardPassing mockedCP = mock(CardPassing.class);
    private HeartsTable mockedHT = mock(HeartsTable.class);
    private VirtualPlayerDecision mockedVPD = mock(VirtualPlayerDecision.class);

    @Before
    public void setUpStream() {
        System.setOut(new PrintStream(changedOutput));
        testHeartsGame.setDb(mockedDB);
        testHeartsGame.setUserInteraction(mockedUA);
        testHeartsGame.setCardPassing(mockedCP);
        testHeartsGame.setHeartsTable(mockedHT);
        testHeartsGame.setVpd(mockedVPD);
    }

    @Test
    public void constructor___should_Create_Hearts_Game_With_Correct_Status() {
        Assert.assertEquals(GameStatus.BEFORE_SETUP, testHeartsGame.getGameStatus());
    }

    @Test
    public void constructor___should_Create_Hearts_Game_With_Correct_Human_Players_Number() {
        assertEquals(2, testHeartsGame.getNumberOfHumanPlayers());
    }

    @Test
    public void constructor___should_Create_Hearts_Game_With_Correct_All_Players_Number() {
        assertEquals(4, testHeartsGame.getNumberOfAllPlayers());
    }

    @Test
    public void constructor___should_Create_Hearts_Game_With_Correct_Players_Names() {
        for (int i = 0; i < testNames.length; i++) {
            assertEquals(testNames[i], testHeartsGame.getPlayers()[i].getName());
        }
        assertEquals("AI #1", testHeartsGame.getPlayers()[2].getName());
        assertEquals("AI #2", testHeartsGame.getPlayers()[3].getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor___should_Not_Create_Hearts_Game_With_More_Than_4_Human_Players() {
        new HeartsGame(5, testNames);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void constructor___should_Not_Create_Hearts_Game_With_Not_Enough_Names_Given() {
        new HeartsGame(3, testNames);
    }

    @Test
    public void dealCards___should_Clear_The_Deck() {
        testHeartsGame.dealCards();

        assertEquals(0, testHeartsGame.getDeck().getCards().size());

    }

    @Test
    public void dealCards___should_Deal_13_Cards_To_Every_Hand() {
        testHeartsGame.dealCards();

        for (Player player : testHeartsGame.getPlayers()) {
            assertEquals(13, player.getCards().size());
        }
    }

    @Test
    public void setUpGame___should_Correctly_Set_Cards_Values() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.BEFORE_SETUP);

        testHeartsGame.setUpGame();

        for (Card card : testHeartsGame.getDeck().getCards()) {
            if (card.getId() == 43) {
                assertEquals(13, card.getValue());
            } else if (card.getColour().equals(CardColour.HEARTS)) {
                assertEquals(1, card.getValue());
            } else {
                assertEquals(0, card.getValue());
            }
        }
    }

    @Test
    public void setUpGame___should_Set_Up_New_Database_Table_In_First_Round() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.BEFORE_SETUP);
        testHeartsGame.setCurrentRound(0);

        testHeartsGame.setUpGame();

        verify(mockedDB, times(1)).setUpNewTable();
    }

    @Test
    public void setUpGame___should_Not_Set_Up_Database_Table_In_Round_Other_Than_1st() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.BEFORE_SETUP);
        int notFirstRoundNumber = (int)Math.floor(Math.random()*20 + 1);
        testHeartsGame.setCurrentRound(notFirstRoundNumber);

        testHeartsGame.setUpGame();

        verify(mockedDB, times(0)).setUpNewTable();
    }

    @Test
    public void setUpGame___should_Correctly_Set_Up_New_Game_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.BEFORE_SETUP);

        testHeartsGame.setUpGame();

        assertEquals(GameStatus.AFTER_SETUP, testHeartsGame.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void setUpGame___should_Not_Set_Up_First_Game_With_Wrong_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);
        testHeartsGame.setUpGame();
    }

    @Test
    public void startTheGame___should_Pass_Cards_If_Game_Rotation_Is_Not_0() throws GameFlowException {
        startTheGame___Setup();
        when(mockedCP.getGameRotation()).thenReturn(2);

        testHeartsGame.startTheGame();

        verify(mockedCP, times(1)).cardPass();
    }

    @Test
    public void startTheGame___should_Not_Pass_Cards_If_Game_Rotation_Is_0() throws GameFlowException {
        startTheGame___Setup();
        when(mockedCP.getGameRotation()).thenReturn(0);

        testHeartsGame.startTheGame();

        verify(mockedCP, never()).cardPass();
    }

    @Test
    public void startTheGame___should_Set_Correct_Current_Player() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.AFTER_SETUP);
        playerFour.getHand().setCards(Arrays.asList(new Card(0), new Card(8)));
        playerOne.getHand().setCards(Arrays.asList(new Card(7), new Card(28)));
        playerThree.getHand().setCards(Arrays.asList(new Card(4), new Card(38)));
        playerTwo.getHand().setCards(Arrays.asList(new Card(10), new Card(18)));

        testHeartsGame.startTheGame();

        new PlayerAssert(playerFour).isTheSamePlayer(testHeartsGame.getCurrentPlayer());
    }

    @Test
    public void startTheGame___should_Correctly_Set_Up_New_Game_Status() throws GameFlowException {
        startTheGame___Setup();

        testHeartsGame.startTheGame();

        assertEquals(GameStatus.PLAYER_READY, testHeartsGame.getGameStatus());
    }

    @Test(expected = NullPointerException.class)
    public void startTheGame___should_Detect_If_Current_Player_Is_Null() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.AFTER_SETUP);
        testHeartsGame.startTheGame();
    }

    @Test(expected = GameFlowException.class)
    public void startTheGame___should_Not_Start_The_Game_With_Wrong_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.BEFORE_SETUP);
        testHeartsGame.startTheGame();
    }

    @Test
    public void makeMove___should_Set_Correct_Game_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);
        playerTwo.getCards().add(new Card(23));
        when(mockedUA.intInputWithCheck("Pick a card: ", 1,
                playerTwo.getHand().getCards().size())).thenReturn(1);
        when(mockedHT.canBePlayed(playerTwo.getHand(), playerTwo.getCard(0),
                testHeartsGame.getCurrentPlayer())).thenReturn(true);

        testHeartsGame.makeMove(playerTwo);

        assertEquals(GameStatus.PLAYER_READY, testHeartsGame.getGameStatus());
    }

    @Test
    public void makeMove___should_Play_Chosen_Card() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);
        Card testCard = new Card(5);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(testCard);
        playerOne.getHand().setCards(cards);

        when(mockedUA.intInputWithCheck("Pick a card: ", 1, playerOne
                .getHand().getCards().size())).thenReturn(1);
        when(mockedHT.canBePlayed(playerOne.getHand(), playerOne.getCard(0),
                testHeartsGame.getCurrentPlayer())).thenReturn(true);

        testHeartsGame.makeMove(playerOne);

        verify(mockedHT, times(1))
                .setCard(playerOne, testCard);
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeMove___should_Not_Run_For_Virtual_Player() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);
        testHeartsGame.makeMove(playerThree);
    }

    @Test(expected = GameFlowException.class)
    public void makeMove___should_Not_Run_With_Wrong_Game_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.AFTER_SETUP);
        testHeartsGame.makeMove(playerTwo);
    }

    @Test
    public void virtualPlayerMove___mockedVPD_Should_Set_Virtual_Player() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);

        testHeartsGame.virtualPlayerMove(playerFour);

        verify(mockedVPD, times(1)).setVirtualPlayer(playerFour);
    }

    @Test
    public void virtualPlayerMove___mockedVPD_Should_Set_Cards_On_Table() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);

        testHeartsGame.virtualPlayerMove(playerFour);

        verify(mockedVPD, times(1)).setCardsOnTable(mockedHT);
    }

    @Test
    public void virtualPlayerMove___mockedVPD_Should_Filter_Playable_Cards() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);

        testHeartsGame.virtualPlayerMove(playerFour);

        verify(mockedVPD, times(1))
                .filterPlayableCards(testHeartsGame.getCurrentPlayer());
    }

    @Test
    public void virtualPlayerMove___mockedVPD_Should_Choose_Card_To_Play() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);

        testHeartsGame.virtualPlayerMove(playerFour);

        verify(mockedVPD, times(1)).chooseCardToPlay();
    }

    @Test
    public void virtualPlayerMove___should_Correctly_Set_Up_New_Game_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);

        testHeartsGame.virtualPlayerMove(playerFour);

        assertEquals(GameStatus.PLAYER_READY, testHeartsGame.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void virtualPlayerMove___should_Not_Run_With_Wrong_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_READY);
        testHeartsGame.virtualPlayerMove(playerThree);
    }

    @Test(expected = IllegalArgumentException.class)
    public void virtualPlayerMove___should_Not_Run_For_Human_Player() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_MOVING);
        testHeartsGame.virtualPlayerMove(playerTwo);
    }

    @Test
    public void moveResult___Should_Display_Correct_Text_To_The_Console() throws GameFlowException {
        moveResult___Setup();

        testHeartsGame.moveResult();

        assertEquals("End of deal\n" +
                "\n" +
                "Cards in game:\n" +
                "This pool goes to TEST PLAYER 1", changedOutput.toString().trim()
                .replace("\r",""));
    }

    @Test
    public void moveResult___Should_Confirm_Twice() throws GameFlowException {
        moveResult___Setup();

        testHeartsGame.moveResult();

        verify(mockedUA, times(2)).confirm();
    }

    @Test
    public void moveResult___Should_Display_Cards_On_The_Table() throws GameFlowException {
        moveResult___Setup();

        testHeartsGame.moveResult();

        verify(mockedHT, times(1)).displayCards(anyString());
    }

    @Test
    public void moveResult___Should_Check_For_Enabling_Hearts() throws GameFlowException {
        moveResult___Setup();

        testHeartsGame.moveResult();

        verify(mockedHT, times(1)).checkForEnablingHearts();
    }

    @Test
    public void moveResult___Should_Collect_Cards_For_Winner() throws GameFlowException {
        moveResult___Setup();
        playerOne.setHand(mock(Hand.class));

        testHeartsGame.moveResult();

        verify(playerOne.getHand(), times(1)).collectCards(mockedHT);
    }

    @Test
    public void moveResult___Should_Set_New_Current_Player() throws GameFlowException {
        moveResult___Setup();

        testHeartsGame.moveResult();

        assertEquals(playerOne, testHeartsGame.getCurrentPlayer());
    }

    @Test
    public void moveResult___Should_Set_New_Game_Status_That_Goes_To_Next_Move() throws GameFlowException {
        moveResult___Setup();
        Card[] cards = {new Card(7), new Card(9)};
        playerOne.getHand().setCards(Arrays.asList(cards));

        testHeartsGame.moveResult();

        assertEquals(GameStatus.PLAYER_READY, testHeartsGame.getGameStatus());
    }

    @Test
    public void moveResult___Should_Set_New_Game_Status_That_Ends_Round() throws GameFlowException {
        moveResult___Setup();

        testHeartsGame.moveResult();

        assertEquals(GameStatus.ROUND_DONE, testHeartsGame.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void moveResult___Should_Not_Run_With_Wrong_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.AFTER_SETUP);
        testHeartsGame.moveResult();
    }

    @Test
    public void printResults___Should_Display_Correct_Text_To_The_Console() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);

        testHeartsGame.printResults();

        assertEquals("Scoreboard", changedOutput.toString().trim().replace("\r",""));
    }

    @Test
    public void printResults___Should_Save_Current_Round_To_The_Table() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);

        testHeartsGame.printResults();

        verify(mockedDB, times(1))
                .saveCurrentRoundToTheTable(testHeartsGame.getCurrentRound(), testHeartsGame.getPlayers());
    }

    @Test
    public void printResults___Should_Display_Table() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);

        testHeartsGame.printResults();

        verify(mockedDB, times(1)).displayTable();
    }

    @Test
    public void printResults___Should_Confirm_Once() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);

        testHeartsGame.printResults();

        verify(mockedUA, times(1)).confirm();
    }

    @Test
    public void printResults___Should_Reset_Settings() throws GameFlowException {
        printResults___Setup();

        testHeartsGame.printResults();

        for (Player player : testHeartsGame.getPlayers()) {
            assertEquals(0, player.getCards().size());
            assertEquals(0, player.getCollectedCards().size());
        }
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Print_Results_With_Wrong_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.PLAYER_READY);
        testHeartsGame.printResults();
    }

    @Test
    public void printFinalScore___should_Display_Correct_Message() throws GameFlowException {
        printFinalScore___Setup();

        testHeartsGame.printFinalScore();

        assertEquals("1. Test Player 1: 69 points\n" +
                "2. AI #2: 77 points\n" +
                "3. Test Player 2: 90 points\n" +
                "4. AI #1: 102 points\n" +
                "\n" +
                "Winner - Test Player 1", changedOutput.toString().trim().replace("\r",""));
    }

    @Test
    public void should_Print_Final_Score() throws GameFlowException {
        printFinalScore___Setup();
        Player[] expectedSortedPlayers = {playerOne, playerFour, playerTwo, playerThree};

        testHeartsGame.printFinalScore();

        assertArrayEquals(expectedSortedPlayers, testHeartsGame.getPlayers());
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Print_Final_Score_With_Wrong_Status() throws GameFlowException {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);
        testHeartsGame.printFinalScore();
    }

    @After
    public void restoreStream() {
        System.setOut(originalOut);
    }

    private void startTheGame___Setup() {
        testHeartsGame.setGameStatus(GameStatus.AFTER_SETUP);
        playerFour.getHand().setCards(Collections.singletonList(new Card(0)));
    }

    private void printResults___Setup() {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);
        int[] cardIds = {43, 2, 6, 10, 14, 18, 22, 26, 30, 34, 38, 42, 46, 50};
        testHeartsGame.setDeck(new Deck(cardIds));
        testHeartsGame.getDeck().setCardValuesByColor(CardColour.HEARTS, 1);
        testHeartsGame.getDeck().setCardValuesByColor(CardColour.SPADES, 13);
        testHeartsGame.getDeck().dealACard(playerOne.getHand());
        testHeartsGame.getDeck().dealACard(playerFour.getHand());

        for (int i = 0; i < 6; i++) {
            testHeartsGame.getDeck().dealACard(playerTwo.getHand());
            testHeartsGame.getDeck().dealACard(playerThree.getHand());
        }

        for (Player player : testHeartsGame.getPlayers()) {
            player.setCollectedCards(player.getCards());
        }
    }

    private void printFinalScore___Setup() {
        testHeartsGame.setGameStatus(GameStatus.GAME_DONE);
        playerOne.setPoints(69);
        playerTwo.setPoints(90);
        playerThree.setPoints(102);
        playerFour.setPoints(77);
    }

    private void moveResult___Setup() {
        testHeartsGame.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
        when(mockedHT.getWinner(testHeartsGame.getPlayers(), testHeartsGame.getCurrentPlayer()))
                .thenReturn(playerOne);
    }
}