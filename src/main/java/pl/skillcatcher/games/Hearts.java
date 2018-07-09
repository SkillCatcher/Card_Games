package pl.skillcatcher.games;

import pl.skillcatcher.cards.Card;
import pl.skillcatcher.cards.CardColour;
import pl.skillcatcher.cards.Deck;
import pl.skillcatcher.cards.Hand;
import pl.skillcatcher.cards.Player;
import pl.skillcatcher.cards.PlayerStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class Hearts extends Game implements Confirmable, SetPlayers, CorrectInputCheck {
    private int currentRound;
    private Card[] pool;
    private boolean heartsAllowed;

    Hearts() {
        this.numberOfAllPlayers = 4;
        this.heartsAllowed = false;
        this.numberOfHumanPlayers = inputWithCheck("Please choose the number of HUMAN players " +
                "- between 0 (if you just want to watch and press enter) and 4 (all human players):", 0, 4);
        this.deck = new Deck();
        this.currentRound = 1;
        this.pool = new Card[numberOfAllPlayers];
        this.players = new Player[numberOfAllPlayers];
        createPlayers(numberOfHumanPlayers, numberOfAllPlayers, players);
    }

    void setCardValues() {
        for (int i = 0; i < 52; i++) {
            if(deck.getACard(i).getId() == 43) {
                deck.getACard(i).setValue(13);
            } else if(deck.getACard(i).getColour() == CardColour.HEARTS) {
                deck.getACard(i).setValue(1);
            } else {
                deck.getACard(i).setValue(0);
            }
        }
    }

    void startTheGame() {
        setCardValues();
        deck.shuffle();
        while (deck.getCards().size() > 0) {
            for (Player player : players) {
                deck.dealACard(player.getHand());
            }
        }

        if (gameRotation() != 0) {
            cardPassTurn();
        }

        currentPlayer = whoGotTwoOfClubs();

        if (currentPlayer != null) {
            while (currentPlayer.getCards().size() > 0) {
                for (int i = currentPlayer.getId(); i < currentPlayer.getId() + 4; i++) {
                    currentSituation(players[i%4]);
                }
                moveResult();
            }
        }
        printResults();
    }

    void currentSituation(Player player) {
        System.out.println(player.getName().toUpperCase() + " - IT'S YOUR TURN"
                + "\nOther players - no peeking :)\n");
        confirm();
        System.out.println("Cards in the game so far:");
        displayPool();
        confirm();

        if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
            makeMove(player);
        } else {
            AI_Move(player);
        }
    }

    void makeMove(Player player) {
        System.out.println(player.getName() + " - your hand:");
        player.getHand().displayHand();

        int choice = inputWithCheck("Pick a card: ", 1, player.getHand().getCards().size());

        if (canBePlayed(player.getHand(), player.getHand().getACard(choice-1))) {
            pool[player.getId()] = player.getHand().playACard(choice);
        } else {
            System.out.println("Sorry - you can't play that card, because:");
            if (!canBePlayed_ColourRule(player.getHand(), player.getHand().getACard(choice-1))) {
                System.out.println("- card's colour doesn't match with color of the first card");
            }

            if (!canBePlayed_HeartsRule(player.getHand(), player.getHand().getACard(choice-1))) {
                System.out.println("- hearts still aren't allowed");
            }

            if (!canBePlayed_FirstRoundRule(player.getHand(), player.getHand().getACard(choice-1))) {
                System.out.println("- it's first deal: "
                        + "\n\ta) it has to start with Two of Clubs"
                        + "\n\tb) cards with points are not allowed");
            }

            System.out.println("\nPlease choose again...");
            confirm();
            makeMove(player);
        }
    }

    void AI_Move(Player playerAI) {
        Hand playableCards = new Hand();

        for (Card card : playerAI.getCards()) {
            if (canBePlayed(playerAI.getHand(), card)) {
                playableCards.getCards().add(card);
            }
        }

        int cardIdChoice = (int)Math.floor(Math.random()*playableCards.getCards().size());
        Card card = playableCards.getACard(cardIdChoice);
        pool[playerAI.getId()] = card;
        playerAI.getCards().remove(card);
    }

    private void moveResult() {
        System.out.println("\nEnd of deal");
        confirm();
        System.out.println("\nCards in game:");
        displayPool();
        Player winner = poolWinner();
        System.out.println("This pool goes to " + winner.getName().toUpperCase());
        confirm();
        checkForEnablingHearts();
        winner.getHand().collectCards(pool);
        currentPlayer = winner;
    }

    private Player poolWinner() {
        int winnerIndex = currentPlayer.getId();
        Card winningCard = pool[currentPlayer.getId()];
        CardColour validColour = pool[currentPlayer.getId()].getColour();
        for (int i = 1; i < pool.length; i++) {
            if(pool[(currentPlayer.getId()+i)%4].getColour().equals(validColour)) {
                if(pool[(currentPlayer.getId()+i)%4].getId() > winningCard.getId()) {
                    winningCard = pool[(currentPlayer.getId()+i)%4];
                    winnerIndex = (currentPlayer.getId()+i)%4;
                }
            }
        }
        return players[winnerIndex];
    }

    private void updatePoints() {
        if (has_26_Points() != null) {
            for (Player player : players) {
                if (!player.equals(has_26_Points())) {
                    player.addPoints(26);
                }
            }
        } else {
            for (Player player : players) {
                for (Card card : player.getCollectedCards()) player.addPoints(card.getValue());
            }
        }
    }

    private Player has_26_Points() {
        for (Player player : players) {
            int sum = 0;
            for (Card card : player.getCollectedCards()) {
                sum += card.getValue();
            }

            if (sum == 26) {
                return player;
            } else if (sum > 0) {
                return null;
            }
        }
        return null;
    }

    void printResults() {
        boolean endGame = false;
        int[] pointsBeforeThisRound = new int[numberOfAllPlayers];
        for (Player player : players) {
            pointsBeforeThisRound[player.getId()] = player.getPoints();
        }
        updatePoints();
        System.out.println("Points after round " + currentRound + ":");
        for (int i = 0; i < players.length; i++) {
            System.out.println((i+1) + ". " + players[i].getName() + ": " + players[i].getPoints()
                    + " (in this round: " + (players[i].getPoints() - pointsBeforeThisRound[i]) + ")");
            if (players[i].getPoints() >= 100) {
                endGame = true;
            }
        }
        confirm();
        if (endGame) {
            printFinalScore();
        } else {
            currentRound++;
            resetGameSettings();
            startTheGame();
        }
    }

    void printFinalScore() {
        sortPlayersByPoints();
        Player winner = players[0];
        int i = 0;
        for (Player player : players) {
            System.out.println((i+1) + ". " + player.getName() + ": " + player.getPoints() + " points");
            i++;
        }
        System.out.println("\nWinner - " + winner.getName());
    }

    private void sortPlayersByPoints() {
        class pointsComparator implements Comparator<Player> {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o1.getPoints(), o2.getPoints());
            }
        }
        Arrays.sort(players, new pointsComparator());
    }

    private Player whoGotTwoOfClubs() {
        for (Player player : players) {
            for (Card card : player.getCards()) {
                if (card.getId() == 0) {
                    return player;
                }
            }
        }
        return null;
    }

    private void cardsPassExecute(Player receivingPlayer, ArrayList<Card> cards) {
        for (Card card : cards) {
            receivingPlayer.getCards().add(card);
        }
    }

    private void cardPassChoice(Player player, ArrayList<Card> cardSet) {
        final int numberOfCardsToPass = 3;
        int numberOfCardsChosen = 0;

        while (numberOfCardsChosen < numberOfCardsToPass) {
            boolean cardPickedFirstTime = true;

            if (numberOfCardsChosen > 0) {
                System.out.println("Your choices so far...");
                for (int i = 0; i < numberOfCardsChosen; i++) {
                    System.out.println((i+1) + ". " + cardSet.get(i).getName());
                }
            }

            int choice = inputWithCheck("Choose card number " + (numberOfCardsChosen+1) + ":",
                        1, 13);

            for (int i = 0; i < numberOfCardsChosen; i++) {
                if (cardSet.get(i).equals(player.getCard(choice-1))) {
                    cardPickedFirstTime = false;
                    break;
                }
            }

            if (cardPickedFirstTime) {
                cardSet.add(player.getCard(choice - 1));
                numberOfCardsChosen++;
            } else {
                System.out.println("Choice of " + player.getCard(choice-1).getName()
                        + " was cancelled.");
                cardSet.remove(player.getCard(choice-1));
                numberOfCardsChosen--;
            }
        }
    }

    private void cardPassTurn() {
        ArrayList<ArrayList<Card>> cardSets = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            cardSets.add(i, new ArrayList<>());

            System.out.println("Card Pass Turn for player: " + players[i].getName());
            confirm();

            if (players[i].getPlayerStatus().equals(PlayerStatus.USER)) {
                System.out.println("Your hand:");
                players[i].getHand().displayHand();

                System.out.println("Choose 3 cards from the list by their number (if you want to reverse the pick, " +
                        "simply pick the same card again). They'll be passed to "
                        + players[( i + gameRotation() ) % 4].getName() + ": \n");

                cardPassChoice(players[i], cardSets.get(i));
                printCardChoices(cardSets.get(i));
                confirm();

                for (Card card : cardSets.get(i)) {
                    players[i].getCards().remove(card);
                }
            } else if (players[i].getPlayerStatus().equals(PlayerStatus.AI)) {
                for (int a = 0; a < 3; a++) {
                    Card card = players[i].getCard((int)Math.floor(Math.random()*players[i].getCards().size()));
                    cardSets.get(i).add(card);
                    players[i].getCards().remove(card);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            cardsPassExecute(players[(i + gameRotation()) % 4], cardSets.get(i));
        }
    }

    private void printCardChoices(ArrayList<Card> cardSet) {
        System.out.println("You've chosen: ");
        for (Card card : cardSet) System.out.println(card.getName());
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

    private void checkForEnablingHearts() {
        for (Card card : pool) {
            if (card.getColour().equals(CardColour.HEARTS)) {
                heartsAllowed = true;
            }
        }
    }

    private boolean canBePlayed(Hand hand, Card card) {
        return canBePlayed_ColourRule(hand, card) && canBePlayed_HeartsRule(hand, card)
                && canBePlayed_FirstRoundRule(hand, card);
    }

    private boolean canBePlayed_ColourRule(Hand hand, Card card) {
        if (Arrays.stream(pool).noneMatch(cardInPool -> cardInPool != null)) {
            return true;
        } else if (pool[currentPlayer.getId()].getColour().equals(card.getColour())) {
            return true;
        } else {
            return hand.getCards().stream()
                    .noneMatch(cardInHand -> cardInHand.getColour().
                            equals(pool[currentPlayer.getId()].getColour()));
        }
    }

    private boolean canBePlayed_HeartsRule(Hand hand, Card card) {
        if (Arrays.stream(pool).noneMatch(cardInPool -> cardInPool != null)) {
            if (heartsAllowed || containsOnlyHearts(hand)) {
                return true;
            } else {
                return !card.getColour().equals(CardColour.HEARTS);
            }
        } else {
            return true;
        }

    }

    private boolean canBePlayed_FirstRoundRule(Hand hand, Card card) {
        if (hand.getCards().size() < 13 || containsOnlyCardsWithPoints(hand)) {
            return true;
        } else if (card.getColour().equals(CardColour.HEARTS) || card.getId() == 43) {
            return false;
        } else if (containsTwoOfClubs(hand)) {
            return card.getId() == 0;
        } else {
            return true;
        }
    }

    private boolean containsTwoOfClubs(Hand hand) {
        for (Card card : hand.getCards()) {
            if (card.getId() == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean containsOnlyCardsWithPoints(Hand hand) {
        for (Card card : hand.getCards()) {
            if (!(card.getId() == 43 || card.getColour().equals(CardColour.HEARTS))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsOnlyHearts(Hand hand) {
        for (Card card : hand.getCards()) {
            if (!(card.getColour().equals(CardColour.HEARTS))) {
                return false;
            }
        }
        return true;
    }

    private void displayPool() {
        for (int i = 0; i < pool.length; i++) {
            if (pool[i] == null) {
                System.out.println((i+1) + ". ---");
            } else {
                System.out.println((i+1) + ". " + pool[i].getName() + " (" + players[i].getName() + ")");
            }
        }
    }

    private void resetGameSettings() {
        heartsAllowed = false;
        deck = new Deck();
        for (Player player : players) {
            player.setHand(new Hand());
        }
    }
}
