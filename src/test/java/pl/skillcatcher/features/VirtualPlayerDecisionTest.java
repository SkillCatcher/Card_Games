package pl.skillcatcher.features;

import org.junit.Before;
import org.junit.Test;
import pl.skillcatcher.asserts.CardsAssert;
import pl.skillcatcher.games.CardsOnTable;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class VirtualPlayerDecisionTest {

    private VirtualPlayerDecision testVPD = new VirtualPlayerDecision();
    private Player testCurrentPlayer = new Player("current", 0);
    private Player testVirtualPlayer = new Player("test", 1, PlayerStatus.AI);
    private CardsOnTable testCoT = new CardsOnTable() {
        @Override
        public Player getWinner(Player[] players, Player currentPlayer) {
            return null;
        }

        @Override
        public boolean canBePlayed(Hand hand, Card card, Player currentPlayer) {
            return card.getColour().equals(getCard(currentPlayer.getId()).getColour());
        }
    };
    private ArrayList<Card> cards = new ArrayList<>();
    private Card[] cardsToChoose = {new Card(4), new Card(8)};

    @Before
    public void setup() {
        int[] cardIds = {3,4,5,6,7,8,9};
        for (int i : cardIds) {
            cards.add(new Card(i));
        }
        testVirtualPlayer.getHand().setCards(cards);
        testVPD.setCardsOnTable(testCoT);
        testVPD.setVirtualPlayer(testVirtualPlayer);
    }

    @Test(expected = NullPointerException.class)
    public void filterPlayableCards___Should_Not_Run_When_VirtualPlayer_Is_Null() {
        testVPD.setVirtualPlayer(null);
        testVPD.filterPlayableCards(testCurrentPlayer);
    }

    @Test(expected = NullPointerException.class)
    public void filterPlayableCards___Should_Not_Run_When_HeartsOnTable_Is_Null() {
        testVPD.setCardsOnTable(null);
        testVPD.filterPlayableCards(testCurrentPlayer);
    }

    @Test
    public void filterPlayableCards___Should_Run_Correctly() {
        testVPD.getCardsOnTable().setCard(testCurrentPlayer, new Card(0));

        testVPD.filterPlayableCards(testCurrentPlayer);

        new CardsAssert(testVPD.getPlayableCards().getCards())
                .areTheseTheSameCards(Arrays.asList(cardsToChoose));
    }

    @Test(expected = NullPointerException.class)
    public void chooseCardToPlay___Should_Not_Run_When_VirtualPlayer_Is_Null() {
        testVPD.setVirtualPlayer(null);
        testVPD.getPlayableCards().setCards(Arrays.asList(cardsToChoose));

        testVPD.chooseCardToPlay();
    }

    @Test(expected = NullPointerException.class)
    public void chooseCardToPlay___Should_Not_Run_When_HeartsOnTable_Is_Null() {
        testVPD.setCardsOnTable(null);
        testVPD.getPlayableCards().setCards(Arrays.asList(cardsToChoose));

        testVPD.chooseCardToPlay();
    }

    @Test
    public void chooseCardToPlay___Should_Choose_Card_From_Playable_Cards() {
        testVPD.getPlayableCards().setCards(Arrays.asList(cardsToChoose));
        testVPD.chooseCardToPlay();

        assertTrue(Arrays.asList(cardsToChoose).contains(testVPD.getCardsOnTable()
                .getCard(testVirtualPlayer.getId())));
    }

    @Test
    public void chooseCardToPlay___Should_Remove_Card_From_Virtual_Players_Hand() {
        testVPD.getPlayableCards().setCards(Arrays.asList(cardsToChoose));
        testVPD.chooseCardToPlay();

        assertFalse(testVirtualPlayer.getCards().contains(testVPD.getCardsOnTable()
                .getCard(testVirtualPlayer.getId())));
    }
}