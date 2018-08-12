package pl.skillcatcher.games;

import pl.skillcatcher.features.*;
import pl.skillcatcher.databases.BlackjackDB;
import pl.skillcatcher.exceptions.GameFlowException;
import pl.skillcatcher.interfaces.PlayersCreator;

import java.util.ArrayList;
import java.util.Arrays;

class BlackJack extends Game implements PlayersCreator {

    private int roundsToPlay;
    private Player dealer;
    private ArrayList<Player> notFinishedPlayers;
    private ArrayList<Player> listOfPlayersToRemove;
    private BlackjackDB db;

    int getRoundsToPlay() {
        return roundsToPlay;
    }

    void setRoundsToPlay(int roundsToPlay) {
        this.roundsToPlay = roundsToPlay;
    }

    Player getDealer() {
        return dealer;
    }

    ArrayList<Player> getNotFinishedPlayers() {
        return notFinishedPlayers;
    }

    void setNotFinishedPlayers(ArrayList<Player> notFinishedPlayers) {
        this.notFinishedPlayers = notFinishedPlayers;
    }

    ArrayList<Player> getListOfPlayersToRemove() {
        return listOfPlayersToRemove;
    }

    void setListOfPlayersToRemove(ArrayList<Player> listOfPlayersToRemove) {
        this.listOfPlayersToRemove = listOfPlayersToRemove;
    }

    void setDb(BlackjackDB db) {
        this.db = db;
    }

    BlackJack(int numberOfHumanPlayers, int roundsToPlay, String[] playersNames) {
        setDeck(new Deck());
        setNumberOfHumanPlayers(numberOfHumanPlayers);
        setNumberOfAllPlayers(getNumberOfHumanPlayers());

        this.roundsToPlay = roundsToPlay;
        setCurrentRound(roundsToPlay - this.roundsToPlay);

        this.dealer = new Player("Dealer", -1, PlayerStatus.AI);
        this.notFinishedPlayers = new ArrayList<>();
        this.listOfPlayersToRemove = new ArrayList<>();
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
        if (!getGameStatus().equals(GameStatus.BEFORE_SETUP)) {
            throw new GameFlowException("Can't set up the game");
        }
        getDeck().setCardValuesByNumber(CardNumber.ACE, 11);
        getDeck().setCardValuesById(36, 47, 10);
        for (int i = 0; i < 36; i++) {
            getDeck().setSingleCardValueById(i, (i/4)+2);
        }

        if (getCurrentRound() == 0) {
            db.setUpNewTable();
        }

        setCurrentRound(getCurrentRound()+1);
        dealCards();

        setCurrentPlayer(getPlayers()[roundsToPlay%getNumberOfHumanPlayers()]);
        addToListFromCurrentPlayer(getPlayers(), notFinishedPlayers, getCurrentPlayer());
        setGameStatus(GameStatus.AFTER_SETUP);
    }

    @Override
    public void startTheGame() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.AFTER_SETUP)) {
            throw new GameFlowException("Can't continue the game");
        }
        removePlayersFromList(notFinishedPlayers, listOfPlayersToRemove);

        if (notFinishedPlayers.size() == 0) {
            System.out.println("\nDealer's turn...\n");
            setGameStatus(GameStatus.ALL_PLAYERS_DONE);
        } else {
            setGameStatus(GameStatus.PLAYER_READY);
        }
    }

    private void addToListFromCurrentPlayer(Player[] allPlayers, ArrayList<Player> list, Player current) {
        for (int i = current.getId(); i < allPlayers.length + current.getId(); i++) {
            list.add(allPlayers[i % allPlayers.length]);
        }
    }

    private void removePlayersFromList(ArrayList<Player> biggerList, ArrayList<Player> smallerList) {
        for (Player player : smallerList) {
            biggerList.remove(player);
        }
    }

    @Override
    public void currentSituation(Player player) throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.PLAYER_READY)) {
            throw new GameFlowException("Can't continue the game");
        }
        System.out.println(player.getName().toUpperCase() + " - IT'S YOUR TURN\n");
        getUserInteraction().confirm();

        displayOpponentsHands(player);

        System.out.println("You can see, that dealer's got a " +
                dealer.getCard(0).getName() + ".\n");

        decreaseAceValue(player);

        System.out.println("Your current hand: ");
        player.getHand().displayHand();
        player.getHand().displayPoints();

        if(failCheck(player)) {
            System.out.println("Oops... your currently have " + player.getHand().getPoints() +
                    " points, which is more than 21. You've lost...");

            listOfPlayersToRemove.add(player);
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
                System.out.println("Hand of " + player.getName());
                player.getHand().displayHand();
                player.getHand().displayPoints();
            }
        }
    }

    @Override
    public void makeMove(Player player) throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.PLAYER_MOVING)) {
            throw new GameFlowException("Can't continue the game");
        }
        int choice = getUserInteraction().intInputWithCheck("Do you want a hit or do you want to stay? " +
                "[Press 1 or 2, and confirm with ENTER]\n" +
                "1 - Hit me! (Draw another card)\n" +
                "2 - I'm good - I'll stay (End your turn)", 1, 2);

        switch (choice) {
            case 1:
                getDeck().dealACard(player.getHand());
                setGameStatus(GameStatus.PLAYER_READY);
                break;
            case 2:
                System.out.println("You've finished with " + player.getHand().getPoints() + " points.\n");
                getUserInteraction().confirm();
                listOfPlayersToRemove.add(player);
                setGameStatus(GameStatus.PLAYER_READY);
                break;
            default:
                break;
        }
    }

    @Override
    public void virtualPlayerMove(Player player) throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.ALL_PLAYERS_DONE)) {
            throw new GameFlowException("Can't continue the game");
        }
        getUserInteraction().confirm();
        decreaseAceValue(player);
        System.out.println("\nDealer currently has this hand: ");
        player.getHand().displayHand();
        player.getHand().displayPoints();

        virtualPlayerDecision(player, player.getHand().getPoints());
    }

    private void virtualPlayerDecision(Player player, int playerPoints) {
        if (playerPoints < 17) {
            System.out.println("Dealer draws another card...");
            getDeck().dealACard(player.getHand());
        } else {
            System.out.println("Dealer ends game with " + player.getHand().getPoints() + " points");
            setGameStatus(GameStatus.ROUND_DONE);
        }
    }

    @Override
    public void printResults() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.ROUND_DONE)) {
            throw new GameFlowException("Can't continue the game");
        }
        ArrayList<Player> winners = new ArrayList<>();
        System.out.println("\nResults:\n");
        getUserInteraction().confirm();

        System.out.println(dealer.getName() + ": " + dealer.getHand().getPoints() + " points");

        for (Player player : getPlayers()) {
            System.out.println(player.getName() + ": " + player.getHand().getPoints() + " points");
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

        db.saveCurrentRoundIntoTable(getCurrentRound(), getPlayers(), dealer);
        db.displayTable();

        System.out.println("\nWinners of this round:");
        for (Player player : winners) {
            System.out.println(player.getName());
        }
        roundsToPlay--;

        if (roundsToPlay > 0) {
            setGameStatus(GameStatus.BEFORE_SETUP);
            resetSettings();
            System.out.println("\n" + roundsToPlay + " rounds left...");
        } else {
            setGameStatus(GameStatus.GAME_DONE);
        }
    }

    private void resetSettings() {
        setDeck(new Deck());
        for (Player player : getPlayers()) {
            player.getCards().clear();
        }
        dealer.getCards().clear();
        notFinishedPlayers.clear();
        listOfPlayersToRemove.clear();
    }

    @Override
    public void printFinalScore() throws GameFlowException {
        if (!getGameStatus().equals(GameStatus.GAME_DONE)) {
            throw new GameFlowException("Can't print final score");
        }
        System.out.println("\nFinal result:");
        getUserInteraction().confirm();
        dealer.setPoints(dealer.getPoints() / getNumberOfHumanPlayers());
        Player winner;
        String verdict;

        if (getPlayers().length == 1) {
            System.out.println("\n" + getPlayers()[0].getName() + ": " + getPlayers()[0].getPoints() + " points");
            if (dealer.getPoints() > getPlayers()[0].getPoints()) {
                winner = dealer;
            } else {
                winner = getPlayers()[0];
            }
            verdict = "\nWINNER: " + winner.getName().toUpperCase() + "!!!";
        } else {
            Arrays.sort(getPlayers(), (Player p1, Player p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));

            int scoreboardIndex = 1;
            for (Player player : getPlayers()) {
                System.out.println(scoreboardIndex + ". " + player.getName() + ": "
                        + player.getPoints() + " points");
                scoreboardIndex++;
            }

            winner = getPlayers()[0];

            if (winner.getPoints() == getPlayers()[1].getPoints() && dealer.getPoints() <= winner.getPoints()) {
                StringBuilder winners = new StringBuilder("\nTIED GAME!!! THE WINNERS ARE:");
                for (Player player : getPlayers()) {
                    if (player.getPoints() == winner.getPoints()) {
                        winners.append("\n- ").append(player.getName()).append("!!!");
                    } else {
                        break;
                    }
                }
                verdict = winners.toString();
            } else {
                if (dealer.getPoints() > getPlayers()[0].getPoints()) {
                    winner = dealer;
                }
                verdict = "\nWINNER: " + winner.getName().toUpperCase() + "!!!";
            }
        }

        System.out.println("\nDealers points: " + dealer.getPoints());
        System.out.println(verdict);
    }
}
