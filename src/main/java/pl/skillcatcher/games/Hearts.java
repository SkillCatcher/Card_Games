package pl.skillcatcher.games;

import pl.skillcatcher.cards.*;
import pl.skillcatcher.databases.HeartsDB;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.interfaces.CorrectIntInputCheck;
import pl.skillcatcher.interfaces.PlayersCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Hearts extends Game implements PlayersCreator, CorrectIntInputCheck {
    private Card[] pool;
    private boolean heartsAllowed;
    private HeartsDB db;

    public Hearts(int numberOfHumanPlayers, String[] playersNames) {
        setGameStatus(GameStatus.BEFORE_SETUP);
        setNumberOfAllPlayers(4);
        this.heartsAllowed = false;
        setNumberOfHumanPlayers(numberOfHumanPlayers);
        setDeck(new Deck());
        setCurrentRound(1);
        this.pool = new Card[getNumberOfAllPlayers()];
        setPlayers(new Player[getNumberOfAllPlayers()]);
        if (numberOfHumanPlayers > playersNames.length) {
            throw new ArrayIndexOutOfBoundsException("List of names (" + playersNames.length +
                    " names) is too short for " + numberOfHumanPlayers + " players...");
        } else {
            createPlayers(getPlayers(), playersNames);
            String[] columnNames = {
                    getPlayers()[0].getName(),
                    getPlayers()[1].getName(),
                    getPlayers()[2].getName(),
                    getPlayers()[3].getName()
            };
            db = new HeartsDB(columnNames);
        }

    }

    @Override
    public void setUpGame() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
            throw new GameFlowException("Can't continue the game");
        }
        getDeck().setAllCardValues(0);
        getDeck().setCardValuesByColor(CardColour.HEARTS, 1);
        getDeck().setSingleCardValueById(43, 13);

        if (getCurrentRound() == 1) {
            db.setUpNewTable();
        }

        dealCards();
        setGameStatus(GameStatus.AFTER_SETUP);
    }

    @Override
    void dealCards() {
        getDeck().shuffle();
        while (getDeck().getCards().size() > 0) {
            for (Player player : getPlayers()) {
                getDeck().dealACard(player.getHand());
            }
        }
    }

    @Override
    void startTheGame() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.AFTER_SETUP)) {
            throw new GameFlowException("Can't continue the game");
        }

        if (gameRotation() != 0) {
            cardPassTurn();
        }

        setCurrentPlayer(whoGotTwoOfClubs());

        if (getCurrentPlayer() != null) {
            setGameStatus(GameStatus.PLAYER_READY);
        } else {
            throw new NullPointerException("No Current Player detected...");
        }
    }

    @Override
    void currentSituation(Player player) throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.PLAYER_READY)) {
            throw new GameFlowException("Can't continue the game");
        }

        System.out.println(player.getName().toUpperCase() + " - IT'S YOUR TURN"
                + "\nOther players - no peeking :)\n");
        getUserAttention().confirm();

        if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
            System.out.println("Cards in the game so far:");
            displayPool();
            getUserAttention().confirm();
            System.out.println(player.getName() + " - your hand:");
            player.getHand().displayHand();
        }

        setGameStatus(GameStatus.PLAYER_MOVING);
    }

    @Override
    void makeMove(Player player) throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.PLAYER_MOVING)) {
            throw new GameFlowException("Can't continue the game");
        }

        int choice = intInputWithCheck("Pick a card: ", 1, player.getHand().getCards().size());

        while (!canBePlayed(player.getHand(), player.getHand().getACard(choice-1))) {
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
            getUserAttention().confirm();
            choice = intInputWithCheck("Pick a card: ", 1, player.getHand().getCards().size());
        }

        pool[player.getId()] = player.getHand().playACard(choice);
        setGameStatus(GameStatus.PLAYER_READY);
    }

    @Override
    void virtualPlayerMove(Player playerAI) throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.PLAYER_MOVING)) {
            throw new GameFlowException("Can't continue the game");
        }

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

        setGameStatus(GameStatus.PLAYER_READY);
    }

    void moveResult() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.ALL_PLAYERS_DONE)) {
            throw new GameFlowException("Can't continue the game");
        }

        System.out.println("\nEnd of deal");
        getUserAttention().confirm();
        System.out.println("\nCards in game:");
        displayPool();
        Player winner = poolWinner();
        System.out.println("This pool goes to " + winner.getName().toUpperCase());
        getUserAttention().confirm();
        checkForEnablingHearts();
        winner.getHand().collectCards(pool);
        setCurrentPlayer(winner);

        if (getCurrentPlayer().getCards().size() > 0) {
            setGameStatus(GameStatus.PLAYER_READY);
        } else {
            setGameStatus(GameStatus.ROUND_DONE);
        }
    }

    private Player poolWinner() {
        int winnerIndex = getCurrentPlayer().getId();
        Card winningCard = pool[getCurrentPlayer().getId()];
        CardColour validColour = pool[getCurrentPlayer().getId()].getColour();
        for (int i = 1; i < pool.length; i++) {
            if(pool[(getCurrentPlayer().getId()+i)%4].getColour().equals(validColour)) {
                if(pool[(getCurrentPlayer().getId()+i)%4].getId() > winningCard.getId()) {
                    winningCard = pool[(getCurrentPlayer().getId()+i)%4];
                    winnerIndex = (getCurrentPlayer().getId()+i)%4;
                }
            }
        }
        return getPlayers()[winnerIndex];
    }

    private void updatePoints() {
        if (has_26_Points() != null) {
            for (Player player : getPlayers()) {
                if (!player.equals(has_26_Points())) {
                    player.addPoints(26);
                }
            }
        } else {
            for (Player player : getPlayers()) {
                for (Card card : player.getCollectedCards()) player.addPoints(card.getValue());
            }
        }
    }

    private Player has_26_Points() {
        for (Player player : getPlayers()) {
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

    @Override
    void printResults() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.ROUND_DONE)) {
            throw new GameFlowException("Can't continue the game");
        }
        boolean endGame = false;
        int[] pointsBeforeThisRound = new int[getNumberOfAllPlayers()];
        for (Player player : getPlayers()) {
            pointsBeforeThisRound[player.getId()] = player.getPoints();
        }
        updatePoints();
        System.out.println("Points after round " + getCurrentRound() + ":");
        for (int i = 0; i < getPlayers().length; i++) {
            System.out.println((i+1) + ". " + getPlayers()[i].getName() + ": " + getPlayers()[i].getPoints()
                    + " (in this round: " + (getPlayers()[i].getPoints() - pointsBeforeThisRound[i]) + ")");
            if (getPlayers()[i].getPoints() >= 100) {
                endGame = true;
            }
        }

        db.saveCurrentRoundToTheTable(getCurrentRound(), getPlayers());
        db.displayTable();

        getUserAttention().confirm();
        if (endGame) {
            setGameStatus(GameStatus.GAME_DONE);
        } else {
            setCurrentRound(getCurrentRound() + 1);
            resetGameSettings();
            setGameStatus(GameStatus.BEFORE_SETUP);
        }
    }

    @Override
    void printFinalScore() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.GAME_DONE)) {
            throw new GameFlowException("Can't continue the game");
        }
        sortPlayersByPoints();
        Player winner = getPlayers()[0];
        int i = 0;
        for (Player player : getPlayers()) {
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
        Arrays.sort(getPlayers(), new pointsComparator());
    }

    private Player whoGotTwoOfClubs() {
        for (Player player : getPlayers()) {
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

            int choice = intInputWithCheck("Choose card number " + (numberOfCardsChosen+1) + ":",
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

            System.out.println("Card Pass Turn for player: " + getPlayers()[i].getName());
            getUserAttention().confirm();

            if (getPlayers()[i].getPlayerStatus().equals(PlayerStatus.USER)) {
                System.out.println("Your hand:");
                getPlayers()[i].getHand().displayHand();

                System.out.println("Choose 3 cards from the list by their number (if you want to reverse the pick, " +
                        "simply pick the same card again). They'll be passed to "
                        + getPlayers()[( i + gameRotation() ) % 4].getName() + ": \n");

                cardPassChoice(getPlayers()[i], cardSets.get(i));
                printCardChoices(cardSets.get(i));
                getUserAttention().confirm();

                for (Card card : cardSets.get(i)) {
                    getPlayers()[i].getCards().remove(card);
                }
            } else if (getPlayers()[i].getPlayerStatus().equals(PlayerStatus.AI)) {
                for (int a = 0; a < 3; a++) {
                    Card card = getPlayers()[i].getCard(
                            (int)Math.floor(Math.random()*getPlayers()[i].getCards().size()));
                    cardSets.get(i).add(card);
                    getPlayers()[i].getCards().remove(card);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            cardsPassExecute(getPlayers()[(i + gameRotation()) % 4], cardSets.get(i));
        }
    }

    private void printCardChoices(ArrayList<Card> cardSet) {
        System.out.println("You've chosen: ");
        for (Card card : cardSet) System.out.println(card.getName());
    }

    private int gameRotation() {
        switch(getCurrentRound()%4) {
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
        } else if (pool[getCurrentPlayer().getId()].getColour().equals(card.getColour())) {
            return true;
        } else {
            return hand.getCards().stream()
                    .noneMatch(cardInHand -> cardInHand.getColour().
                            equals(pool[getCurrentPlayer().getId()].getColour()));
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
                System.out.println((i+1) + ". " + pool[i].getName() + " (" + getPlayers()[i].getName() + ")");
            }
        }
    }

    private void resetGameSettings() {
        heartsAllowed = false;
        setDeck(new Deck());
        for (Player player : getPlayers()) {
            player.setHand(new Hand());
        }
    }
}
