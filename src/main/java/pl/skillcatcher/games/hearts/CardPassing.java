package pl.skillcatcher.games.hearts;

import pl.skillcatcher.features.Card;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.PlayerStatus;
import pl.skillcatcher.features.UserInteraction;
import pl.skillcatcher.games.ConsoleMessages;

import java.util.ArrayList;

public class CardPassing {
    private ConsoleMessages messages = new ConsoleMessages();
    private UserInteraction userInteraction;
    private ArrayList<ArrayList<Card>> cardTrios;
    private Player[] players;
    private int currentRound;

    public void setUserInteraction(UserInteraction userInteraction) {
        this.userInteraction = userInteraction;
    }

    public ArrayList<ArrayList<Card>> getCardTrios() {
        return cardTrios;
    }

    public void setCardTrios(ArrayList<ArrayList<Card>> cardTrios) {
        this.cardTrios = cardTrios;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    CardPassing(Player[] players, int currentRound) {
        this.players = players;
        this.currentRound = currentRound;
        this.userInteraction = new UserInteraction();
        this.cardTrios = new ArrayList<>();
        for (Player player : players) {
            cardTrios.add(new ArrayList<>());
        }
    }

    private int gameRotation() {
        switch(currentRound%4) {
            case 1:
                return 1;
            case 2:
                return 3;
            case 3:
                return 2;
            default:
                return 0;
        }
    }

    public int getGameRotation() {
        return gameRotation();
    }

    private void cardPassTurn(Player player) {
        System.out.println(String.format(messages.HEARTS_PASSING_START, player.getName()));
        userInteraction.confirm();

        if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
            player.getHand().displayHand(messages.YOUR_HAND);
            String receivingPlayer = getPlayers()[( player.getId() + gameRotation() ) % 4].getName();
            System.out.println(String.format(messages.HEARTS_CHOOSE_3_CARDS, receivingPlayer));

            cardPassChoice(player, cardTrios.get(player.getId()));
            printCardChoices(cardTrios.get(player.getId()));
            userInteraction.confirm();

            for (Card card : cardTrios.get(player.getId())) {
                player.getCards().remove(card);
            }
        } else {
            for (int a = 0; a < 3; a++) {
                Card card = player.getCard(
                        (int)Math.floor(Math.random()*player.getCards().size()));
                cardTrios.get(player.getId()).add(card);
                getPlayers()[player.getId()].getCards().remove(card);
            }
        }
    }

    private void printCardChoices(ArrayList<Card> cardTrio) {
        System.out.println(messages.HEARTS_3_CHOSEN_CARDS);
        for (Card card : cardTrio) System.out.println(card.getName());
    }

    private void cardsPassExecute(Player receivingPlayer, ArrayList<Card> cardTrio) {
        for (Card card : cardTrio) {
            receivingPlayer.getCards().add(card);
        }
    }

    private void cardPassChoice(Player player, ArrayList<Card> cardTrio) {
        while (cardTrio.size() < 3) {
            partialChoiceSummary(cardTrio);
            String singleChoiceRequest = String.format(messages.HEARTS_CHOOSE_A_CARD, cardTrio.size()+1);
            int choice = userInteraction.intInputWithCheck(singleChoiceRequest, 1, 13);

            if (!isCardPickedAgain(player, cardTrio, choice-1)) {
                cardTrio.add(player.getCard(choice - 1));
            } else {
                cardChoiceCancellation(player, choice-1, cardTrio);
            }
        }
    }

    private void partialChoiceSummary(ArrayList<Card> cardTrio) {
        if (cardTrio.size() > 0) {
            System.out.println(messages.HEARTS_PARTIAL_CHOICE);
            for (int i = 0; i < cardTrio.size(); i++) {
                System.out.println((i+1) + ". " + cardTrio.get(i).getName());
            }
        }
    }

    private boolean isCardPickedAgain(Player player, ArrayList<Card> cardTrio, int cardID) {
        for (int i = 0; i < cardTrio.size(); i++) {
            if (cardTrio.get(i).equals(player.getCard(cardID))) {
                return true;
            }
        }
        return false;
    }

    private void cardChoiceCancellation(Player player, int cardID, ArrayList<Card> cardTrio) {
        System.out.println(String.format(messages.HEARTS_CHOICE_CANCELLED, player.getCard(cardID).getName()));
        cardTrio.remove(player.getCard(cardID));
    }

    public void cardPass() {
        for (Player player : getPlayers()) {
            cardPassTurn(player);
        }

        for (Player player : getPlayers()) {
            cardsPassExecute(getPlayers()[(player.getId() + getGameRotation()) % 4],
                    getCardTrios().get(player.getId()));
        }
    }
}
