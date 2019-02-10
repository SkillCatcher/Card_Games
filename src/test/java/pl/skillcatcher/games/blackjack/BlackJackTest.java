package pl.skillcatcher.games.blackjack;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.skillcatcher.asserts.PlayerAssert;
import pl.skillcatcher.features.*;
import pl.skillcatcher.databases.BlackjackDB;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.games.GameStatus;
import pl.skillcatcher.games.blackjack.BlackJack;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BlackJackTest { //TODO: REPAIR THE TESTS LIKE HEARTS
    private final ByteArrayOutputStream changedOutput = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private String[] testNames = {"Mr. A", "Mr. B", "Mr. C"};
    private Player testDealer = new Player("Dealer", -1, PlayerStatus.AI);
    private BlackJack testBlackJack = new BlackJack(3, 5, testNames);
    private BlackjackDB mockedDB = mock(BlackjackDB.class);
    private UserInteraction mockedUA = mock(UserInteraction.class);
    private Player player = testBlackJack.getPlayers()[0];
    private Player dealer = testBlackJack.getDealer();

    @Before
    public void setUpStream() {
        System.setOut(new PrintStream(changedOutput));
        testBlackJack.setDb(mockedDB);
        testBlackJack.setUserInteraction(mockedUA);
    }


    @Test
    public void should_Create_Blackjack_Game_With_Correct_Number_Of_Human_Players() {
        assertEquals(3, testBlackJack.getNumberOfHumanPlayers());
    }

    @Test
    public void should_Create_Blackjack_Game_With_Correct_Number_Of_Rounds_To_Play() {
        assertEquals(5, testBlackJack.getRoundsToPlay());
    }

    @Test
    public void should_Create_Blackjack_Game_With_Correct_Game_Status() {
        assertEquals(GameStatus.BEFORE_SETUP, testBlackJack.getGameStatus());
    }

    @Test
    public void should_Create_Blackjack_Game_With_Dealer() {
        new PlayerAssert(testBlackJack.getDealer()).isTheSamePlayer(testDealer);
    }

    @Test
    public void should_Create_Blackjack_Game_With_Correct_Player_Names() {
        for (int i = 0; i < testNames.length; i++) {
            assertEquals(testNames[i], testBlackJack.getPlayers()[i].getName());
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void should_Not_Create_Blackjack_Game_With_Incorrect_Parameters() {
        BlackJack wrongBlackjack = new BlackJack(5, 1, testNames);
    }

    @Test
    public void dealCards___Should_Remove_Correct_Number_Of_Cards_From_Deck() {
        testBlackJack.dealCards();
        assertEquals(44, testBlackJack.getDeck().getCards().size());
    }

    @Test
    public void dealCards___Should_Deal_Correct_Number_Of_Cards_To_Dealer() {
        testBlackJack.dealCards();
        assertEquals(2, testBlackJack.getDealer().getCards().size());
    }

    @Test
    public void dealCards___Should_Deal_Correct_Number_Of_Cards_To_Players() {
        testBlackJack.dealCards();
        for (Player player : testBlackJack.getPlayers()) {
            assertEquals(2, player.getCards().size());
        }
    }




//TODO: REPAIR THE TESTS LIKE HEARTS





    @Test
    public void should_Correctly_Set_Up_First_Game() throws GameFlowException {
        Player[] players = testBlackJack.getPlayers();

        testBlackJack.setUpGame();

        cardSetValuesResult();
        if (testBlackJack.getCurrentRound() == 0) {
            verify(mockedDB, times(1)).setUpNewTable();
        }
        assertEquals(1, testBlackJack.getCurrentRound());

        int currentPlayerIndex = testBlackJack.getRoundsToPlay() % testBlackJack.getNumberOfHumanPlayers();
        new PlayerAssert(testBlackJack.getCurrentPlayer())
                .isTheSamePlayer(players[currentPlayerIndex]);

        int currentPlayerId = testBlackJack.getCurrentPlayer().getId();

        assertEquals(GameStatus.AFTER_SETUP, testBlackJack.getGameStatus());
    }

    @Test
    public void should_Correctly_Set_Up_Next_Games() throws GameFlowException {
        Player[] players = testBlackJack.getPlayers();
        int roundNumber = (int)Math.floor(Math.random()*99 + 1);
        testBlackJack.setCurrentRound(roundNumber);

        testBlackJack.setUpGame();

        cardSetValuesResult();
        assertEquals(roundNumber+1, testBlackJack.getCurrentRound());

        int currentPlayerIndex = testBlackJack.getRoundsToPlay() % testBlackJack.getNumberOfHumanPlayers();
        new PlayerAssert(testBlackJack.getCurrentPlayer())
                .isTheSamePlayer(players[currentPlayerIndex]);

        int currentPlayerId = testBlackJack.getCurrentPlayer().getId();

        assertEquals(GameStatus.AFTER_SETUP, testBlackJack.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Set_Up_Game_With_Wrong_Game_Status() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
        testBlackJack.setUpGame();
    }

    @Test
    public void should_Start_The_Game_And_Move_To_Players_Moves() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.AFTER_SETUP);
        ArrayList<Player> testPlayers = new ArrayList<>(Arrays.asList(testBlackJack.getPlayers()));
        ArrayList<Player> expectedResult = new ArrayList<>();
        ArrayList<Player> toRemove = new ArrayList<>();
            toRemove.add(testBlackJack.getPlayers()[1]);
            expectedResult.add(testBlackJack.getPlayers()[0]);
            expectedResult.add(testBlackJack.getPlayers()[2]);

        testBlackJack.startTheGame();
        assertEquals(GameStatus.PLAYER_READY, testBlackJack.getGameStatus());
    }

    @Test
    public void should_Start_The_Game_And_Move_To_Dealers_Move() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.AFTER_SETUP);
        ArrayList<Player> testPlayers = new ArrayList<>(Arrays.asList(testBlackJack.getPlayers()));
        ArrayList<Player> toRemove = new ArrayList<>(Arrays.asList(testBlackJack.getPlayers()));

        testBlackJack.startTheGame();

        assertEquals("Dealer's turn...", changedOutput.toString().trim());
        assertEquals(GameStatus.ALL_PLAYERS_DONE, testBlackJack.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Start_The_Game_With_Wrong_Game_Status() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
        testBlackJack.startTheGame();
    }

    @Test
    public void should_Run_Current_Situation_And_Decrease_Value_Of_Ace() throws GameFlowException {
        currentSituationSetup();
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(44));
        testBlackJack.getDealer().getHand().setCards(cards);
        cards.add(new Card(43));
        cards.add(new Card(50));
        for (Card card : cards) {
            card.setValue(10);
        }
        player.getHand().setCards(cards);

        testBlackJack.currentSituation(player);

        assertEquals("MR. A - IT'S YOUR TURN\n" +
                "\n" +
                "Hand of Mr. B\n" +
                "Hand of Mr. C\n" +
                "You can see, that dealer's got a King of Clubs.\n" +
                "\n" +
                "Your current hand: \n" +
                "1. King of Clubs\n" +
                "2. Ace of HeartsGame\n" +
                "3. Queen of Spades\n" +
                "Current points: 21", changedOutput.toString().trim().replace("\r",""));
        currentSituationCommonAsserts();
        assertEquals(43, player.getCard(2).getId());
        assertEquals(50, player.getCard(1).getId());
        assertEquals(44, player.getCard(0).getId());
        assertEquals(1, player.getCard(1).getValue());
        assertTrue(player.getHand().getPoints() < 22);
        assertEquals(GameStatus.PLAYER_MOVING, testBlackJack.getGameStatus());
    }

    @Test
    public void should_Run_Current_Situation_And_Check_For_Over_21_Points() throws GameFlowException {
        currentSituationSetup();
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(44));
        testBlackJack.getDealer().getHand().setCards(cards);
        cards.add(new Card(43));
        cards.add(new Card(42));
        for (Card card : cards) {
            card.setValue(10);
        }
        player.getHand().setCards(cards);

        testBlackJack.currentSituation(player);

        assertEquals("MR. A - IT'S YOUR TURN\n" +
                "\n" +
                "Hand of Mr. B\n" +
                "Hand of Mr. C\n" +
                "You can see, that dealer's got a King of Clubs.\n" +
                "\n" +
                "Your current hand: \n" +
                "1. King of Clubs\n" +
                "2. Queen of HeartsGame\n" +
                "3. Queen of Spades\n" +
                "Current points: 30\n" +
                "\n" +
                "Oops... your currently have 30 points, which is more than 21. You've lost...",
                changedOutput.toString().trim().replace("\r",""));
        currentSituationCommonAsserts();
        assertEquals(43, player.getCard(2).getId());
        assertEquals(42, player.getCard(1).getId());
        assertEquals(44, player.getCard(0).getId());
        assertTrue(player.getHand().getPoints() > 21);
    }

    @Test
    public void should_Run_Current_Situation_And_Move_On_To_Player_Move() throws GameFlowException {
        currentSituationSetup();
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(44));
        testBlackJack.getDealer().getHand().setCards(cards);
        cards.add(new Card(43));
        for (Card card : cards) {
            card.setValue(10);
        }
        player.getHand().setCards(cards);

        testBlackJack.currentSituation(player);

        assertEquals("MR. A - IT'S YOUR TURN\n" +
                "\n" +
                "Hand of Mr. B\n" +
                "Hand of Mr. C\n" +
                "You can see, that dealer's got a King of Clubs.\n" +
                "\n" +
                "Your current hand: \n" +
                "1. King of Clubs\n" +
                "2. Queen of Spades\n" +
                "Current points: 20", changedOutput.toString().trim().replace("\r",""));
        currentSituationCommonAsserts();
        assertEquals(43, player.getCard(1).getId());
        assertEquals(44, player.getCard(0).getId());
        assertTrue(player.getHand().getPoints() < 22);
        assertEquals(GameStatus.PLAYER_MOVING, testBlackJack.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Run_Current_Situation_With_Wrong_Game_Status() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.BEFORE_SETUP);
        testBlackJack.currentSituation(testBlackJack.getPlayers()[0]);
    }

    @Test
    public void should_Make_Move_With_Drawing_Another_Card() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.PLAYER_MOVING);
        //System.setIn(new ByteArrayInputStream("1".getBytes()));
        when(mockedUA.intInputWithCheck("Do you want a hit or do you want to stay? [Press 1 or 2, and confirm with ENTER]\n" +
                "1 - Hit me! (Draw another card)\n" +
                "2 - I'm good - I'll stay (End your turn)", 1, 2)).thenReturn(1);

        assertEquals(0, player.getCards().size());

        testBlackJack.makeMove(player);

        assertEquals(1, player.getCards().size());
        assertEquals(GameStatus.PLAYER_READY, testBlackJack.getGameStatus());
    }

    @Test
    public void should_Make_Move_With_Ending_Move() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.PLAYER_MOVING);
        System.setIn(new ByteArrayInputStream("2".getBytes()));

        testBlackJack.makeMove(player);

        assertEquals("Do you want a hit or do you want to stay? [Press 1 or 2, and confirm with ENTER]\n" +
                "1 - Hit me! (Draw another card)\n" +
                "2 - I'm good - I'll stay (End your turn)\n" +
                "You've finished with 0 points.", changedOutput.toString().trim().replace("\r",""));
        verify(mockedUA, times(1)).confirm();
        assertEquals(GameStatus.PLAYER_READY, testBlackJack.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Make_Move_With_Wrong_Game_Status() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.BEFORE_SETUP);
        testBlackJack.makeMove(player);
    }

    @Test
    public void should_Make_Virtual_Player_Move_With_Drawing_Another_Card() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(10));
        cards.add(new Card(33));
        cards.add(new Card(48));
        for (Card card : cards) {
            card.setValue((card.getId()/4)+2);
        }
        dealer.getHand().setCards(cards);

        assertTrue(dealer.getHand().getPoints() > 21);
        testBlackJack.virtualPlayerMove(dealer);

        verify(mockedUA, times(1)).confirm();
        assertEquals(1, dealer.getCard(0).getValue());
        assertTrue(dealer.getHand().getPoints() < 17);
        assertEquals(4, dealer.getCards().size());
        assertEquals("Dealer currently has this hand: \n" +
                "1. Ace of Clubs\n" +
                "2. Ten of Diamonds\n" +
                "3. Four of HeartsGame\n" +
                "Current points: 15\n" +
                "\n" +
                "Dealer draws another card...",
                changedOutput.toString().trim().replace("\r",""));
    }

    @Test
    public void should_Make_Virtual_Player_Move_With_Ending_Move() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.ALL_PLAYERS_DONE);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(47));
        cards.add(new Card(33));
        cards.add(new Card(48));
        for (Card card : cards) {
            card.setValue(10);
        }
        dealer.getHand().setCards(cards);

        assertTrue(dealer.getHand().getPoints() > 21);
        testBlackJack.virtualPlayerMove(dealer);

        verify(mockedUA, times(1)).confirm();
        assertEquals(1, dealer.getCard(0).getValue());
        assertTrue(dealer.getHand().getPoints() > 16);
        assertEquals(3, dealer.getCards().size());
        assertEquals("Dealer currently has this hand: \n" +
                "1. Ace of Clubs\n" +
                "2. Ten of Diamonds\n" +
                "3. King of Spades\n" +
                "Current points: 21\n" +
                "\n" +
                "Dealer ends game with 21 points", changedOutput.toString().trim().replace("\r",""));
        assertEquals(GameStatus.ROUND_DONE, testBlackJack.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Make_Virtual_Player_Move_With_Wrong_Game_Status() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.BEFORE_SETUP);
        testBlackJack.virtualPlayerMove(testBlackJack.getDealer());
    }

    @Test
    public void should_Print_Results_Where_It_Is_Not_Last_Round() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.ROUND_DONE);
        setHandPoints(player, 14);
        setHandPoints(testBlackJack.getPlayers()[1], 17);
        setHandPoints(testBlackJack.getPlayers()[2], 26);
        setHandPoints(dealer, 23);
        testBlackJack.setRoundsToPlay(3);
        testBlackJack.setCurrentRound(3);

        testBlackJack.printResults();

        assertEquals("Results:\n" +
                "\n" +
                "Dealer: 23 points\n" +
                "Mr. A: 14 points\n" +
                "Mr. B: 17 points\n" +
                "Mr. C: 26 points\n" +
                "\n" +
                "Winners of this round:\n" +
                "Mr. A\n" +
                "Mr. B\n" +
                "\n" +
                "2 rounds left...", changedOutput.toString().trim().replace("\r",""));
        verify(mockedUA, times(1)).confirm();
        verify(mockedDB, times(1)).saveCurrentRoundIntoTable(
                testBlackJack.getCurrentRound(),
                testBlackJack.getPlayers(),
                testBlackJack.getDealer());
        verify(mockedDB, times(1)).displayTable();
        assertEquals(1, player.getPoints());
        assertEquals(1, testBlackJack.getPlayers()[1].getPoints());
        assertEquals(0, testBlackJack.getPlayers()[2].getPoints());
        assertEquals(0, dealer.getPoints());
        assertTrue(testBlackJack.getRoundsToPlay() > 0);
        assertEquals(GameStatus.BEFORE_SETUP, testBlackJack.getGameStatus());
        assertEquals(52, testBlackJack.getDeck().getCards().size());
        for (Player player : testBlackJack.getPlayers()) {
            assertEquals(0, player.getCards().size());
        }
        assertEquals(0, dealer.getCards().size());
    }

    @Test
    public void should_Print_Results_Where_It_Is_Last_Round() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.ROUND_DONE);
        setHandPoints(player, 15);
        setHandPoints(testBlackJack.getPlayers()[1], 18);
        setHandPoints(testBlackJack.getPlayers()[2], 21);
        setHandPoints(dealer, 18);
        testBlackJack.setRoundsToPlay(1);
        testBlackJack.setCurrentRound(5);

        testBlackJack.printResults();

        assertEquals("Results:\n" +
                "\n" +
                "Dealer: 18 points\n" +
                "Mr. A: 15 points\n" +
                "Mr. B: 18 points\n" +
                "Mr. C: 21 points\n" +
                "\n" +
                "Winners of this round:\n" +
                "Mr. B\n" +
                "Mr. C", changedOutput.toString().trim().replace("\r",""));
        verify(mockedUA, times(1)).confirm();
        verify(mockedDB, times(1)).saveCurrentRoundIntoTable(
                testBlackJack.getCurrentRound(),
                testBlackJack.getPlayers(),
                testBlackJack.getDealer());
        verify(mockedDB, times(1)).displayTable();
        assertEquals(0, player.getPoints());
        assertEquals(1, testBlackJack.getPlayers()[1].getPoints());
        assertEquals(1, testBlackJack.getPlayers()[2].getPoints());
        assertEquals(2, dealer.getPoints());
        assertEquals(0, testBlackJack.getRoundsToPlay());
        assertEquals(GameStatus.GAME_DONE, testBlackJack.getGameStatus());
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Print_Results_With_Wrong_Game_Status() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.AFTER_SETUP);
        testBlackJack.printResults();
    }

    @Test
    public void should_Print_Final_Score_Where_One_Player_Wins() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.GAME_DONE);
        setFinalScore(6, 4, 2, 14);

        testBlackJack.printFinalScore();

        verify(mockedUA, times(1)).confirm();
        assertEquals("Final result:\n" +
                "1. Mr. A: 6 points\n" +
                "2. Mr. B: 4 points\n" +
                "3. Mr. C: 2 points\n" +
                "\n" +
                "Dealers points: 4\n" +
                "\n" +
                "WINNER: MR. A!!!", changedOutput.toString().trim().replace("\r",""));
    }

    @Test
    public void should_Print_Final_Score_With_Multiple_Winners() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.GAME_DONE);
        setFinalScore(6, 4, 6, 15);

        testBlackJack.printFinalScore();

        verify(mockedUA, times(1)).confirm();
        assertEquals("Final result:\n" +
                "1. Mr. A: 6 points\n" +
                "2. Mr. C: 6 points\n" +
                "3. Mr. B: 4 points\n" +
                "\n" +
                "Dealers points: 5\n" +
                "\n" +
                "TIED GAME!!! THE WINNERS ARE:\n" +
                "- Mr. A!!!\n" +
                "- Mr. C!!!", changedOutput.toString().trim().replace("\r",""));
    }

    @Test
    public void should_Print_Final_Score_When_Dealer_Wins() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.GAME_DONE);
        setFinalScore(3, 3, 2, 16);

        testBlackJack.printFinalScore();

        verify(mockedUA, times(1)).confirm();
        assertEquals("Final result:\n" +
                "1. Mr. A: 3 points\n" +
                "2. Mr. B: 3 points\n" +
                "3. Mr. C: 2 points\n" +
                "\n" +
                "Dealers points: 5\n" +
                "\n" +
                "WINNER: DEALER!!!", changedOutput.toString().trim().replace("\r",""));
    }

    @Test(expected = GameFlowException.class)
    public void should_Not_Print_Final_Score_With_Wrong_Game_Status() throws GameFlowException {
        testBlackJack.setGameStatus(GameStatus.AFTER_SETUP);
        testBlackJack.printFinalScore();
    }

    @After
    public void restoreStream() {
        System.setOut(originalOut);
    }

    private void cardSetValuesResult() {
        for (Card card : testBlackJack.getDeck().getCards()) {
            if (card.getNumber().equals(CardNumber.ACE)) {
                assertEquals(11, card.getValue());
            } else if (card.getId() > 35) {
                assertEquals(10, card.getValue());
            } else {
                int expectedValue = card.getId()/4 + 2;
                assertEquals(expectedValue, card.getValue());
            }
        }
    }

    private void currentSituationSetup() {
        testBlackJack.setGameStatus(GameStatus.PLAYER_READY);
        testBlackJack.getPlayers()[1].setHand(mock(Hand.class));
        testBlackJack.getPlayers()[2].setHand(mock(Hand.class));
    }

    private void currentSituationCommonAsserts() {
        verify(mockedUA, times(1)).confirm();
        verify(testBlackJack.getPlayers()[1].getHand(), times(1)).displayHand(anyString());
        verify(testBlackJack.getPlayers()[1].getHand(), times(1)).displayPoints();
        verify(testBlackJack.getPlayers()[2].getHand(), times(1)).displayHand(anyString());
        verify(testBlackJack.getPlayers()[2].getHand(), times(1)).displayPoints();
    }

    private void setHandPoints(Player player, int points) {
        ArrayList<Card> card = new ArrayList<>();
        card.add(new Card(0));
        player.getHand().setCards(card);
        player.getHand().getACard(0).setValue(points);
    }

    private void setFinalScore(int player1, int player2, int player3, int dealerPoints) {
        player.setPoints(player1);
        testBlackJack.getPlayers()[1].setPoints(player2);
        testBlackJack.getPlayers()[2].setPoints(player3);
        dealer.setPoints(dealerPoints);
    }
}