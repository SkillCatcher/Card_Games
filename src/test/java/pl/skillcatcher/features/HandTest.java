package pl.skillcatcher.features;

import org.junit.Test;
import pl.skillcatcher.asserts.CardsAssert;
import pl.skillcatcher.games.CardsOnTable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class HandTest {
    private Hand hand = new Hand();

    @Test
    public void should_Get_Correct_Amount_Of_Points() {
        Deck deck = new Deck(5, 15);
        hand.setCards(deck.getCards());
        for (Card card : hand.getCards()) {
            card.setValue(card.getId());
        }
        int score = hand.getPoints();
        assertEquals(110, score);
    }

    @Test
    public void should_Play_A_Correct_Card() {
        Card comparableCard = new Card(13);
        Deck comparableDeck = new Deck(5, 15);
        Deck deck = new Deck(5, 15);
        hand.setCards(deck.getCards());
        Card playedCard = hand.playACard(8);

        new CardsAssert(comparableCard).isThisTheSameCard(playedCard);
        assertEquals(hand.getCards().size(), comparableDeck.getCards().size() -1);
    }

    @Test
    public void should_Collect_Cards() {
        Card[] comparablePool = {new Card(16), new Card(36), new Card(2), new Card(13)};
        CardsOnTable pool = new CardsOnTable() {
            @Override
            public Player getWinner(Player[] players, Player currentPlayer) {return null;}
            @Override
            public boolean canBePlayed(Hand hand, Card card, Player currentPlayer) {return false;}
        };

        for (int i = 0; i < comparablePool.length; i++) {
            pool.getCards().put(i, comparablePool[i]);
        }
        hand.collectCards(pool);

        for (int i = 0; i < comparablePool.length; i++) {
            new CardsAssert(hand.getCollectedCards().get(i)).isThisTheSameCard(comparablePool[i]);
            assertNull(pool.getCard(i));
        }
    }

    @Test
    public void should_Display_Sorted_Hand() {
        final ByteArrayOutputStream changedOutput = new ByteArrayOutputStream();
        final PrintStream originalOutput = System.out;
        System.setOut(new PrintStream(changedOutput));
        Deck deck = new Deck(6, 18);
        hand.setCards(deck.getCards());

        hand.displayHand();

        assertEquals("1. Four of Clubs\n" +
                "2. Five of Clubs\n" +
                "3. Six of Clubs\n" +
                "4. Four of Diamonds\n" +
                "5. Five of Diamonds\n" +
                "6. Six of Diamonds\n" +
                "7. Three of Hearts\n" +
                "8. Four of Hearts\n" +
                "9. Five of Hearts\n" +
                "10. Six of Hearts\n" +
                "11. Three of Spades\n" +
                "12. Four of Spades\n" +
                "13. Five of Spades", changedOutput.toString().trim().replace("\r",""));

        System.setOut(originalOutput);
    }

    @Test
    public void should_Sort_Cards() {
        int[] controlIntGroup = {8, 12, 16, 9, 13, 17, 6, 10, 14, 18, 7, 11, 15};
        Deck deck = new Deck(6, 18);
        hand.setCards(deck.getCards());

        hand.displayHand();

        for (int i = 0; i < controlIntGroup.length; i++) {
            new CardsAssert(hand.getACard(i)).isThisTheSameCard(new Card(controlIntGroup[i]));
        }
    }
}