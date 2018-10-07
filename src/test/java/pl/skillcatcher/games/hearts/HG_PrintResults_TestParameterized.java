package pl.skillcatcher.games.hearts;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pl.skillcatcher.databases.HeartsDB;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.features.CardColour;
import pl.skillcatcher.features.Deck;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.games.GameStatus;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class HG_PrintResults_TestParameterized {
    private String[] testNames = {"Test Player 1", "Test Player 2"};
    private HeartsGame testHeartsGame = new HeartsGame(2, testNames);
    private HeartsDB mockedDB = mock(HeartsDB.class);
    private UserInteraction mockedUA = mock(UserInteraction.class);

    private Player playerOne = testHeartsGame.getPlayers()[0];
    private Player playerTwo = testHeartsGame.getPlayers()[1];
    private Player playerThree = testHeartsGame.getPlayers()[2];
    private Player playerFour = testHeartsGame.getPlayers()[3];

    private int basePoints;
    private int p1;
    private int p2;
    private int p3;
    private int p4;
    private GameStatus endStatus;

    public HG_PrintResults_TestParameterized(int basePoints,
                                             int p1, int p2, int p3, int p4, GameStatus endStatus) {
        this.basePoints = basePoints;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.endStatus = endStatus;
    }

    @Before
    public void setup() {
        testHeartsGame.setGameStatus(GameStatus.ROUND_DONE);
        int[] cardIds = {43, 2, 6, 10, 14, 18, 22, 26, 30, 34, 38, 42, 46, 50};
        testHeartsGame.setDeck(new Deck(cardIds));
        testHeartsGame.getDeck().setCardValuesByColor(CardColour.HEARTS, 1);
        testHeartsGame.getDeck().setCardValuesByColor(CardColour.SPADES, 13);
        testHeartsGame.getDeck().dealACard(playerOne.getHand());
        testHeartsGame.getDeck().dealACard(playerFour.getHand());

        testHeartsGame.setDb(mockedDB);
        testHeartsGame.setUserInteraction(mockedUA);

        for (int i = 0; i < 6; i++) {
            testHeartsGame.getDeck().dealACard(playerTwo.getHand());
            testHeartsGame.getDeck().dealACard(playerThree.getHand());
        }

        for (Player player : testHeartsGame.getPlayers()) {
            player.setCollectedCards(player.getCards());
        }
    }

    @Parameterized.Parameters
    public static Collection<Object> testConditions() {
        return Arrays.asList(new Object[][] {
                {0, 13, 6, 6, 1, GameStatus.BEFORE_SETUP},
                {90, 103, 96, 96, 91, GameStatus.GAME_DONE},
                {47, 60, 53, 53, 48, GameStatus.BEFORE_SETUP},
                {99, 112, 105, 105, 100, GameStatus.GAME_DONE}
        });
    }

    @Test
    public void printResults___Should_Update_Points() throws GameFlowException {
        for (Player player : testHeartsGame.getPlayers()) {
            player.setPoints(basePoints);
        }

        testHeartsGame.printResults();

        Assert.assertEquals(p1, playerOne.getPoints());
        Assert.assertEquals(p2, playerTwo.getPoints());
        Assert.assertEquals(p3, playerThree.getPoints());
        Assert.assertEquals(p4, playerFour.getPoints());
    }

    @Test
    public void printResults___Should_Update_Game_Status() throws GameFlowException {
        for (Player player : testHeartsGame.getPlayers()) {
            player.setPoints(basePoints);
        }

        testHeartsGame.printResults();

        Assert.assertEquals(endStatus, testHeartsGame.getGameStatus());
    }


}
