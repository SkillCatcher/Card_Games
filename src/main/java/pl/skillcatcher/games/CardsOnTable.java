package pl.skillcatcher.games;

import pl.skillcatcher.features.Card;
import pl.skillcatcher.features.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CardsOnTable {
    private Map<Integer, Card> cards;

    public Map<Integer, Card> getCards() {
        return cards;
    }

    public void setCards(Map<Integer, Card> cards) {
        this.cards = cards;
    }

    public CardsOnTable() {
        this.cards = new LinkedHashMap<>();
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    public void setCard(Player player, Card card) {
        this.cards.put(player.getId(), card);
    }

    public void removeCard(int index) {
        this.cards.remove(index);
    }

    public void clear() {
        this.cards.clear();
    }

    abstract public Player getWinner(Player[] players, Player currentPlayer);

    public void displayCards() {
        for (int i = 0; i < 4; i++) {
            if (getCard(i) == null) {
                System.out.println((i+1) + ". ---");
            } else {
                System.out.println((i+1) + ". " + getCard(i).getName());
            }
        }
    }
}
