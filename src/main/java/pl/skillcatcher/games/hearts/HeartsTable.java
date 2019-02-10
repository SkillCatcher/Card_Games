package pl.skillcatcher.games.hearts;

import pl.skillcatcher.features.Card;
import pl.skillcatcher.features.CardColour;
import pl.skillcatcher.features.Hand;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.games.CardsOnTable;

public class HeartsTable extends CardsOnTable {
    private boolean heartsAllowed;

    public HeartsTable() {
        super();
        this.heartsAllowed = false;
    }

    public void setHeartsAllowed(boolean heartsAllowed) {
        this.heartsAllowed = heartsAllowed;
    }

    @Override
    public Player getWinner(Player[] players, Player currentPlayer) {
        int winnerIndex = currentPlayer.getId();
        Card winningCard = getCard(winnerIndex);
        CardColour validColour = winningCard.getColour();

        for (int i = 1; i < getCards().size(); i++) {
            int comparedPlayerId = (currentPlayer.getId() + i) % 4;
            if (isCurrentWinningCardBeaten(comparedPlayerId, validColour, winningCard)) {
                winnerIndex = comparedPlayerId;
                winningCard = getCard(winnerIndex);
            }
        }
        return players[winnerIndex];
    }

    private boolean isCurrentWinningCardBeaten(int challengerID, CardColour winningColour, Card currentWinningCard) {
        return getCard(challengerID).getColour().equals(winningColour)
                && getCard(challengerID).getId() > currentWinningCard.getId();
    }

    public void checkForEnablingHearts() {
        for (int i = 0; i < getCards().size(); i++) {
            Card card = getCard(i);
            if (card.getColour().equals(CardColour.HEARTS)) {
                heartsAllowed = true;
            }
        }
    }

    public boolean canBePlayed(Hand hand, Card card, Player currentPlayer) {
        return canBePlayed_ColourRule(hand, card, currentPlayer)
                && canBePlayed_HeartsRule(hand, card)
                && canBePlayed_FirstRoundRule(hand, card);
    }

    public boolean canBePlayed_ColourRule(Hand hand, Card card, Player currentPlayer) {
        if (getCards().isEmpty()) {
            return true;
        } else {
            return canBePlayed_ColourRule_TableNotEmptyCase(currentPlayer, hand, card);
        }
    }

    private boolean canBePlayed_ColourRule_TableNotEmptyCase(Player roundStarter, Hand hand, Card card) {
        CardColour validColor = getCard(roundStarter.getId()).getColour();
        if (card.getColour().equals(validColor)) {
            return true;
        } else {
            return hand.getCards().stream().noneMatch(cardInHand -> cardInHand.getColour().equals(validColor));
        }
    }

    public boolean canBePlayed_HeartsRule(Hand hand, Card card) {
        if (getCards().isEmpty()) {
            return canBePlayed_HeartsRule_EmptyTableCase(hand, card);
        } else {
            return true;
        }
    }

    private boolean canBePlayed_HeartsRule_EmptyTableCase(Hand hand, Card card) {
        if (heartsAllowed || hand.containsOnlyOneColor(CardColour.HEARTS)) {
            return true;
        } else {
            return !card.getColour().equals(CardColour.HEARTS);
        }
    }

    public boolean canBePlayed_FirstRoundRule(Hand hand, Card card) {
        if (hand.getCards().size() < 13 || containsOnlyCardsWithPoints(hand)) {
            return true;
        } else if (card.getColour().equals(CardColour.HEARTS) || card.getId() == 43) {
            return false;
        } else if (hand.containsCard(0)) {
            return card.getId() == 0;
        } else {
            return true;
        }
    }

    private boolean containsOnlyCardsWithPoints(Hand hand) {
        for (Card card : hand.getCards()) {
            if (!(card.getId() == 43 || card.getColour().equals(CardColour.HEARTS))) {
                return false;
            }
        }
        return true;
    }
}
