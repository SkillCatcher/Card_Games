package pl.skillcatcher.features;

import pl.skillcatcher.games.CardsOnTable;

import java.util.ArrayList;

public class VirtualPlayerDecision {
    private Player virtualPlayer;
    private Hand playableCards;
    private CardsOnTable cardsOnTable;

    public Player getVirtualPlayer() {
        return virtualPlayer;
    }

    public void setVirtualPlayer(Player virtualPlayer) {
        this.virtualPlayer = virtualPlayer;
    }

    public Hand getPlayableCards() {
        return playableCards;
    }

    public void setPlayableCards(Hand playableCards) {
        this.playableCards = playableCards;
    }

    public CardsOnTable getCardsOnTable() {
        return cardsOnTable;
    }

    public void setCardsOnTable(CardsOnTable cardsOnTable) {
        this.cardsOnTable = cardsOnTable;
    }

    public VirtualPlayerDecision(Player virtualPlayer, CardsOnTable cardsOnTable) {
        this.virtualPlayer = virtualPlayer;
        this.playableCards = new Hand();
        this.cardsOnTable = cardsOnTable;
    }

    public void filterPlayableCards(Player currentPlayer) {
        ArrayList<Card> playable = new ArrayList<>();
        for (Card card : virtualPlayer.getCards()) {
            if ((cardsOnTable.canBePlayed(virtualPlayer.getHand(), card, currentPlayer))) {
                playable.add(card);
            }
        }
        playableCards.setCards(playable);
    }

    public void chooseCardToPlay(CardsOnTable cardsOnTable) {
        int cardIdChoice = (int)Math.floor(Math.random()*playableCards.getCards().size());
        Card card = playableCards.getACard(cardIdChoice);
        cardsOnTable.setCard(virtualPlayer, card);
        virtualPlayer.getCards().remove(card);
    }
}
