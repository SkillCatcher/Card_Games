package pl.skillcatcher.games.hearts;

import pl.skillcatcher.features.Card;
import pl.skillcatcher.features.Player;
import pl.skillcatcher.features.PlayerStatus;
import pl.skillcatcher.features.UserInteraction;

import java.util.ArrayList;

public class CardPassing {
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
        System.out.println("Card Pass Turn for player: " + player.getName());
        userInteraction.confirm();

        if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
            System.out.println("Your hand:");
            player.getHand().displayHand();

            System.out.println("Choose 3 cards from the list by their number (if you want to reverse the pick, " +
                    "simply pick the same card again). They'll be passed to "
                    + getPlayers()[( player.getId() + gameRotation() ) % 4].getName() + ": \n");

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
        System.out.println("You've chosen: ");
        for (Card card : cardTrio) System.out.println(card.getName());
    }

    private void cardsPassExecute(Player receivingPlayer, ArrayList<Card> cardTrio) {
        for (Card card : cardTrio) {
            receivingPlayer.getCards().add(card);
        }
    }

    private void cardPassChoice(Player player, ArrayList<Card> cardTrio) {
        final int numberOfCardsToPass = 3;
        int numberOfCardsChosen = 0;

        while (numberOfCardsChosen < numberOfCardsToPass) {
            boolean cardPickedFirstTime = true;

            if (numberOfCardsChosen > 0) {
                System.out.println("Your choices so far...");
                for (int i = 0; i < numberOfCardsChosen; i++) {
                    System.out.println((i+1) + ". " + cardTrio.get(i).getName());
                }
            }

            int choice = userInteraction.intInputWithCheck("Choose card number " +
                            (numberOfCardsChosen+1) + ":", 1, 13);

            for (int i = 0; i < numberOfCardsChosen; i++) {
                if (cardTrio.get(i).equals(player.getCard(choice-1))) {
                    cardPickedFirstTime = false;
                    break;
                }
            }

            if (cardPickedFirstTime) {
                cardTrio.add(player.getCard(choice - 1));
                numberOfCardsChosen++;
            } else {
                System.out.println("Choice of " + player.getCard(choice-1).getName()
                        + " was cancelled.");
                cardTrio.remove(player.getCard(choice-1));
                numberOfCardsChosen--;
            }
        }
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
