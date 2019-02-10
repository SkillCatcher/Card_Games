package pl.skillcatcher.games.blackjack;

import pl.skillcatcher.features.*;
import pl.skillcatcher.databases.BlackjackDB;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.games.ConsoleMessages;
import pl.skillcatcher.games.Game;
import pl.skillcatcher.games.GameStatus;
import pl.skillcatcher.games.PlayersInGame;
import pl.skillcatcher.interfaces.PlayersCreator;

import java.util.ArrayList;
import java.util.Arrays;

public class BlackJack extends Game implements PlayersCreator {
    private ConsoleMessages messages = new ConsoleMessages();
    private int roundsToPlay;
    private Player dealer;
    private PlayersInGame playersInGame;
    private BlackjackDB db;

    int getRoundsToPlay() {
        return roundsToPlay;
    }

    void setRoundsToPlay(int roundsToPlay) {
        this.roundsToPlay = roundsToPlay;
    }

    public Player getDealer() {
        return dealer;
    }

    public PlayersInGame getPlayersInGame() {
        return playersInGame;
    }

    public void setPlayersInGame(PlayersInGame playersInGame) {
        this.playersInGame = playersInGame;
    }

    void setDb(BlackjackDB db) {
        this.db = db;
    }

    public BlackJack(int numberOfHumanPlayers, int roundsToPlay, String[] playersNames) {
        setDeck(new Deck());
        setNumberOfHumanPlayers(numberOfHumanPlayers);
        setNumberOfAllPlayers(getNumberOfHumanPlayers());

        this.roundsToPlay = roundsToPlay;
        setCurrentRound(roundsToPlay - this.roundsToPlay);

        this.dealer = new Player("Dealer", -1, PlayerStatus.AI);
        this.playersInGame = new PlayersInGame();
        setGameStatus(GameStatus.BEFORE_SETUP);

        setPlayers(new Player[getNumberOfAllPlayers()]);
        if (numberOfHumanPlayers > playersNames.length) {
            throw new ArrayIndexOutOfBoundsException("List of names (" + playersNames.length +
                    " names) is too short for " + numberOfHumanPlayers + " players...");
        } else {
            createPlayers(getPlayers(), playersNames);
            db = new BlackjackDB(playersNames);
        }
    }

    @Override
    public void dealCards() {
        getDeck().shuffle();
        for (Player player : getPlayers()) {
            getDeck().dealACard(player.getHand());
            getDeck().dealACard(player.getHand());
        }
        getDeck().dealACard(dealer.getHand());
        getDeck().dealACard(dealer.getHand());
    }

    @Override
    public void setUpGame() throws GameFlowException {
        checkStatus(GameStatus.BEFORE_SETUP);
        getDeck().setCardValuesByNumber(CardNumber.ACE, 11);
        getDeck().setCardValuesById(36, 47, 10);
        for (int i = 0; i < 36; i++) getDeck().setSingleCardValueById(i, (i/4)+2);

        if (getCurrentRound() == 0) db.setUpNewTable();
        setCurrentRound(getCurrentRound()+1);
        dealCards();

        setCurrentPlayer(getPlayers()[roundsToPlay%getNumberOfHumanPlayers()]);
        playersInGame.fillInPlayersInGameList(getPlayers(), getCurrentPlayer());
        setGameStatus(GameStatus.AFTER_SETUP);
    }

    @Override
    public void startTheGame() throws GameFlowException {
        checkStatus(GameStatus.AFTER_SETUP);
        playersInGame.removePlayersFromList();

        if (playersInGame.getNotFinishedPlayers().size() == 0) {
            System.out.println("\nDealer's turn...\n");
            setGameStatus(GameStatus.ALL_PLAYERS_DONE);
        } else {
            setGameStatus(GameStatus.PLAYER_READY);
        }
    }

    @Override
    public void currentSituation(Player player) throws GameFlowException {
        checkStatus(GameStatus.PLAYER_READY);
        System.out.println(String.format(messages.TURN_START, player.getName().toUpperCase()));
        getUserInteraction().confirm();

        displayOpponentsHands(player);
        System.out.println(String.format(messages.BLACKJACK_DEALER_CARD, dealer.getCard(0).getName()));
        decreaseAceValue(player);
        player.getHand().displayHand(messages.YOUR_HAND);
        player.getHand().displayPoints();

        if(failCheck(player)) {
            System.out.println(String.format(messages.BLACKJACK_OVER_21, player.getHand().getPoints()));
            playersInGame.addToRemove(player);
        } else {
            setGameStatus(GameStatus.PLAYER_MOVING);
        }
    }

    private boolean failCheck(Player player) {
        return player.getHand().getPoints() > 21;
    }

    private void decreaseAceValue(Player player) {
        for (int i = 0; i < player.getCards().size(); i++) {
            if (failCheck(player) && player.getCard(i).getNumber().equals(CardNumber.ACE) &&
                    player.getCard(i).getValue() != 1) {
                player.getCard(i).setValue(1);
                break;
            }
        }
    }

    private void displayOpponentsHands(Player yourPlayer) {
        for (Player player : getPlayers()) {
            if (!player.equals(yourPlayer)) {
                player.getHand().displayHand(String.format(messages.BLACKJACK_OTHER_HAND, player.getName()));
                player.getHand().displayPoints();
            }
        }
    }

    @Override
    public void makeMove(Player player) throws GameFlowException {
        checkStatus(GameStatus.PLAYER_MOVING);
        int choice = getUserInteraction().intInputWithCheck(messages.BLACKJACK_DECISION, 1, 2);
        switch (choice) {
            case 1:
                getDeck().dealACard(player.getHand());
                setGameStatus(GameStatus.PLAYER_READY);
                break;
            case 2:
                System.out.println(String.format(messages.BLACKJACK_STAY, player.getHand().getPoints()));
                getUserInteraction().confirm();
                playersInGame.addToRemove(player);
                setGameStatus(GameStatus.PLAYER_READY);
                break;
            default:
                break;
        }
    }

    @Override
    public void virtualPlayerMove(Player player) throws GameFlowException {
        checkStatus(GameStatus.ALL_PLAYERS_DONE);
        getUserInteraction().confirm();
        decreaseAceValue(player);
        player.getHand().displayHand(messages.BLACKJACK_DEALER_HAND);
        player.getHand().displayPoints();
        virtualPlayerDecision(player, player.getHand().getPoints());
    }

    private void virtualPlayerDecision(Player player, int playerPoints) {
        if (playerPoints < 17) {
            System.out.println(messages.BLACKJACK_DEALER_DRAW);
            getDeck().dealACard(player.getHand());
        } else {
            System.out.println(String.format(messages.BLACKJACK_DEALER_STAY, player.getHand().getPoints()));
            setGameStatus(GameStatus.ROUND_DONE);
        }
    }

    @Override
    public void printResults() throws GameFlowException {
        checkStatus(GameStatus.ROUND_DONE);
        System.out.println(messages.BLACKJACK_RESULTS);
        getUserInteraction().confirm();

        displayCurrentRoundPoints();
        ArrayList<Player> winners = currentRoundWinners();
        db.saveCurrentRoundIntoTable(getCurrentRound(), getPlayers(), dealer);
        db.displayTable();

        System.out.println(messages.BLACKJACK_ROUND_WINNERS);
        for (Player player : winners) {
            System.out.println(player.getName());
        }
        roundsToPlay--;

        if (roundsToPlay > 0) {
            setGameStatus(GameStatus.BEFORE_SETUP);
            resetSettings();
            System.out.println(String.format(messages.BLACKJACK_RESET, roundsToPlay));
        } else {
            setGameStatus(GameStatus.GAME_DONE);
        }
    }

    private void displayCurrentRoundPoints() {
        System.out.println(String.format(messages.SCOREBOARD_ROW, dealer.getName(), dealer.getHand().getPoints()));
        for (Player player : getPlayers()) {
            System.out.println(String.format(messages.SCOREBOARD_ROW, player.getName(), player.getHand().getPoints()));
        }
    }

    private ArrayList<Player> currentRoundWinners() {
        ArrayList<Player> winners = new ArrayList<>();
        for (Player player : getPlayers()) {
            if ( !failCheck(player) &&
                    (player.getHand().getPoints() > dealer.getHand().getPoints() || failCheck(dealer)) ) {
                winners.add(player);
                player.addPoints(1);
            } else if ( !failCheck(dealer) &&
                    (player.getHand().getPoints() < dealer.getHand().getPoints() || failCheck(player)) ) {
                dealer.addPoints(1);
            } else if (player.getHand().getPoints() == dealer.getHand().getPoints() && !failCheck(player)
                    && !failCheck(dealer)){
                winners.add(player);
                player.addPoints(1);
                dealer.addPoints(1);
            }
        }
        return winners;
    }

    private void resetSettings() {
        setDeck(new Deck());
        for (Player player : getPlayers()) {
            player.getCards().clear();
        }
        dealer.getCards().clear();
        playersInGame.clear();
    }

    @Override
    public void printFinalScore() throws GameFlowException {
        checkStatus(GameStatus.GAME_DONE);
        System.out.println(messages.BLACKJACK_FINAL_RESULTS);
        getUserInteraction().confirm();
        dealer.setPoints(dealer.getPoints() / getNumberOfHumanPlayers());
        System.out.println(String.format(messages.BLACKJACK_DEALER_FINAL_POINTS, dealer.getPoints()));

        if (getPlayers().length == 1) {
            System.out.println(singlePlayerFinalResults());
        } else {
            System.out.println(multiPlayerFinalResults());
        }
    }

    private String singlePlayerFinalResults() {
        Player winner;
        Player player = getPlayers()[0];
        System.out.println(String.format("\n" + messages.SCOREBOARD_ROW, player.getName(), player.getPoints()));
        if (dealer.getPoints() > getPlayers()[0].getPoints()) {
            winner = dealer;
        } else {
            winner = player;
        }
        return String.format(messages.BLACKJACK_WINNER, winner.getName().toUpperCase());
    }

    private String multiPlayerFinalResults() {
        Arrays.sort(getPlayers(), (Player p1, Player p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));
        displayFinalScoreboard();
        Player winner = getPlayers()[0];

        if (winner.getPoints() == getPlayers()[1].getPoints() && dealer.getPoints() <= winner.getPoints()) {
            return displayWinnersInTiedGame(winner.getPoints()).toString();
        } else {
            if (dealer.getPoints() > getPlayers()[0].getPoints()) winner = dealer;
            return String.format(messages.BLACKJACK_WINNER, winner.getName().toUpperCase());
        }
    }

    private void displayFinalScoreboard() {
        int scoreboardIndex = 1;
        for (Player player : getPlayers()) {
            String scoreboardEntry = String.format(messages.SCOREBOARD_ROW, player.getName(), player.getPoints());
            System.out.println(scoreboardIndex + ". " + scoreboardEntry);
            scoreboardIndex++;
        }
    }

    private StringBuilder displayWinnersInTiedGame(int winningPoints) {
        StringBuilder winners = new StringBuilder("\nTIED GAME!!! THE WINNERS ARE:");
        for (Player player : getPlayers()) {
            if (player.getPoints() == winningPoints) {
                winners.append("\n- ").append(player.getName()).append("!!!");
            } else {
                break;
            }
        }
        return winners;
    }

    private void checkStatus(GameStatus gameStatus) throws GameFlowException {
        if (!getGameStatus().equals(gameStatus)) {
            throw new GameFlowException("Can't continue the game, because status is " + gameStatus);
        }
    }
}