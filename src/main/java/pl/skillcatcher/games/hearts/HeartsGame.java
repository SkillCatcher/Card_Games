package pl.skillcatcher.games.hearts;

import pl.skillcatcher.features.*;
import pl.skillcatcher.databases.HeartsDB;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.games.ConsoleMessages;
import pl.skillcatcher.games.Game;
import pl.skillcatcher.games.GameStatus;
import pl.skillcatcher.interfaces.PlayersCreator;

import java.util.Arrays;
import java.util.Comparator;

public class HeartsGame extends Game implements PlayersCreator {
    private ConsoleMessages messages = new ConsoleMessages();
    private HeartsDB db;
    private HeartsTable heartsTable;
    private CardPassing cardPassing;
    private VirtualPlayerDecision vpd;

    public HeartsTable getHeartsTable() {
        return heartsTable;
    }

    public void setHeartsTable(HeartsTable heartsTable) {
        this.heartsTable = heartsTable;
    }

    public void setDb(HeartsDB db) {
        this.db = db;
    }

    public CardPassing getCardPassing() {
        return cardPassing;
    }

    public void setCardPassing(CardPassing cardPassing) {
        this.cardPassing = cardPassing;
    }

    public VirtualPlayerDecision getVpd() {
        return vpd;
    }

    public void setVpd(VirtualPlayerDecision vpd) {
        this.vpd = vpd;
    }

    public HeartsGame(int numberOfHumanPlayers, String[] playersNames) {
        setGameStatus(GameStatus.BEFORE_SETUP);
        setNumberOfAllPlayers(4);

        if (numberOfHumanPlayers > getNumberOfAllPlayers()) {
            throw new IllegalArgumentException("Too much human players - 4 is maximum");
        } else {
            setNumberOfHumanPlayers(numberOfHumanPlayers);
        }

        setDeck(new Deck());
        setCurrentRound(0);
        setPlayers(new Player[getNumberOfAllPlayers()]);
        this.heartsTable = new HeartsTable();
        this.vpd = new VirtualPlayerDecision();

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
        checkGameStatus(GameStatus.BEFORE_SETUP);
        setCurrentRound(getCurrentRound() + 1);
        getDeck().setAllCardValues(0);
        getDeck().setCardValuesByColor(CardColour.HEARTS, 1);
        getDeck().setSingleCardValueById(43, 13);

        if (getCurrentRound() == 1) {
            db.setUpNewTable();
        }

        cardPassing = new CardPassing(getPlayers(), getCurrentRound());
        dealCards();
        setGameStatus(GameStatus.AFTER_SETUP);
    }

    @Override
    public void dealCards() {
        getDeck().shuffle();
        while (getDeck().getCards().size() > 0) {
            for (Player player : getPlayers()) {
                getDeck().dealACard(player.getHand());
            }
        }
    }

    @Override
    public void startTheGame() throws GameFlowException {
        checkGameStatus(GameStatus.AFTER_SETUP);

        if (cardPassing.getGameRotation() != 0) {
            cardPassing.cardPass();
        }

        setCurrentPlayer(whoGotTwoOfClubs());

        if (getCurrentPlayer() != null) {
            setGameStatus(GameStatus.PLAYER_READY);
        } else {
            throw new NullPointerException("No Current Player detected...");
        }
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

    @Override
    public void currentSituation(Player player) throws GameFlowException {
        checkGameStatus(GameStatus.PLAYER_READY);
        System.out.println(String.format(messages.TURN_START, player.getName().toUpperCase()));
        getUserInteraction().confirm();

        if (player.getPlayerStatus().equals(PlayerStatus.USER)) {
            heartsTable.displayCards(messages.HEARTS_CARDS_ON_TABLE);
            getUserInteraction().confirm();
            player.getHand().displayHand(player.getName() + " - " + messages.YOUR_HAND);
        }
        setGameStatus(GameStatus.PLAYER_MOVING);
    }

    @Override
    public void makeMove(Player player) throws GameFlowException {
        checkPlayerStatus(PlayerStatus.USER, player);
        checkGameStatus(GameStatus.PLAYER_MOVING);

        int choice = getUserInteraction()
                .intInputWithCheck(messages.CARD_PICK, 1, player.getHand().getCards().size());

        while (!heartsTable.canBePlayed(player.getHand(), player.getCard(choice-1), getCurrentPlayer())) {
            System.out.println(messages.CARD_PICK_FAIL);

            if (!heartsTable.canBePlayed_ColourRule(player.getHand(), player.getCard(choice-1),
                    getCurrentPlayer())) {
                System.out.println(messages.CARD_PICK_FAIL_COLOR_RULE);
            }

            if (!heartsTable.canBePlayed_HeartsRule(player.getHand(), player.getHand().getACard(choice-1))) {
                System.out.println(messages.HEARTS_ALLOWED_RULE);
            }

            if (!heartsTable.canBePlayed_FirstRoundRule(player.getHand(), player.getHand().getACard(choice-1))) {
                System.out.println(messages.HEARTS_FIRST_DEAL_RULE);
            }

            System.out.println(messages.NEW_CHOICE);
            choice = getUserInteraction().intInputWithCheck(messages.CARD_PICK, 1,
                    player.getHand().getCards().size());
        }

        heartsTable.setCard(player, player.getHand().playACard(choice-1));
        setGameStatus(GameStatus.PLAYER_READY);
    }

    @Override
    public void virtualPlayerMove(Player playerAI) throws GameFlowException {
        checkPlayerStatus(PlayerStatus.AI, playerAI);
        checkGameStatus(GameStatus.PLAYER_MOVING);
        vpd.setVirtualPlayer(playerAI);
        vpd.setCardsOnTable(heartsTable);
        vpd.filterPlayableCards(getCurrentPlayer());
        vpd.chooseCardToPlay();
        setGameStatus(GameStatus.PLAYER_READY);
    }

    public void moveResult() throws GameFlowException {
        checkGameStatus(GameStatus.ALL_PLAYERS_DONE);
        System.out.println(messages.END_OF_DEAL);
        getUserInteraction().confirm();

        heartsTable.displayCards(messages.CARDS_IN_GAME);
        Player winner = heartsTable.getWinner(getPlayers(), getCurrentPlayer());
        System.out.println(String.format(messages.POOL_WINNER, winner.getName().toUpperCase()));
        getUserInteraction().confirm();

        heartsTable.checkForEnablingHearts();
        winner.getHand().collectCards(heartsTable);
        setCurrentPlayer(winner);

        if (getCurrentPlayer().getCards().size() > 0) {
            setGameStatus(GameStatus.PLAYER_READY);
        } else {
            setGameStatus(GameStatus.ROUND_DONE);
        }
    }

    private void updatePoints() {
        for (Player player : getPlayers()) {
            if (has_26_Points() == null) {
                player.addPoints(collectedPoints(player));
            } else if (!player.equals(has_26_Points())) {
                player.addPoints(26);
            }
        }
    }

    private Player has_26_Points() {
        for (Player player : getPlayers()) {
            int sum = collectedPoints(player);
            if (sum == 26) {
                return player;
            } else if (sum > 0) {
                return null;
            }
        }
        return null;
    }

    private int collectedPoints(Player player) {
        int sum = 0;
        for (Card card : player.getCollectedCards()) {
            sum += card.getValue();
        }
        return sum;
    }

    @Override
    public void printResults() throws GameFlowException {
        checkGameStatus(GameStatus.ROUND_DONE);
        updatePoints();
        System.out.println(messages.SCOREBOARD);

        db.saveCurrentRoundToTheTable(getCurrentRound(), getPlayers());
        db.displayTable();
        getUserInteraction().confirm();

        if (Arrays.stream(getPlayers()).anyMatch(player -> player.getPoints() >= 100)) {
            setGameStatus(GameStatus.GAME_DONE);
        } else {
            resetGameSettings();
            setGameStatus(GameStatus.BEFORE_SETUP);
        }
    }

    @Override
    public void printFinalScore() throws GameFlowException {
        checkGameStatus(GameStatus.GAME_DONE);
        sortPlayersByPoints();
        Player winner = getPlayers()[0];
        int i = 0;
        for (Player player : getPlayers()) {
            System.out.println(String.format(
                    messages.SCOREBOARD_ROW_WITH_NUM, (i+1), player.getName(), player.getPoints()));
            i++;
        }
        System.out.println(String.format(messages.SCOREBOARD_LEADER, winner.getName()));
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

    private void resetGameSettings() {
        heartsTable.setHeartsAllowed(false);
        setDeck(new Deck());
        for (Player player : getPlayers()) {
            player.setHand(new Hand());
        }
    }

    private void checkGameStatus(GameStatus gameStatus) throws GameFlowException {
        if (!getGameStatus().equals(gameStatus)) {
            throw new GameFlowException("Can't continue the game");
        }
    }

    private void checkPlayerStatus(PlayerStatus playerStatus, Player player) {
        if (!player.getPlayerStatus().equals(playerStatus)) {
            throw new IllegalArgumentException("Wrong kind of player");
        }
    }
}
