package pl.skillcatcher.games;

import pl.skillcatcher.cards.*;
import pl.skillcatcher.databases.BlackjackDB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class BlackJack extends Game implements Confirmable, PlayersCreator, CorrectIntInputCheck {

    private int roundsToPlay;
    private Player dealer;
    private ArrayList<Player> notFinishedPlayers;
    private ArrayList<Player> listOfPlayersToRemove;
    private BlackjackDB db;

    public int getRoundsToPlay() {
        return roundsToPlay;
    }

    public void setRoundsToPlay(int roundsToPlay) {
        this.roundsToPlay = roundsToPlay;
    }

    Player getDealer() {
        return dealer;
    }

    public void setDealer(Player dealer) {
        this.dealer = dealer;
    }

    ArrayList<Player> getNotFinishedPlayers() {
        return notFinishedPlayers;
    }

    public void setNotFinishedPlayers(ArrayList<Player> notFinishedPlayers) {
        this.notFinishedPlayers = notFinishedPlayers;
    }

    public ArrayList<Player> getListOfPlayersToRemove() {
        return listOfPlayersToRemove;
    }

    public void setListOfPlayersToRemove(ArrayList<Player> listOfPlayersToRemove) {
        this.listOfPlayersToRemove = listOfPlayersToRemove;
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
        setPlayers(new Player[getNumberOfAllPlayers()]);
        if (numberOfHumanPlayers > playersNames.length) {
            throw new ArrayIndexOutOfBoundsException("List of names (" + playersNames.length +
                    " names) is too short for " + numberOfHumanPlayers + " players...");
        } else {
            createPlayers(getPlayers(), playersNames);
            db = new BlackjackDB(playersNames);
        }
    }

    void setCardValues() {
        for (int i = 0; i < 52; i++) {
            if(i < 36) {
                getDeck().getACard(i).setValue((i/4)+2);
            } else if (i < 48) {
                getDeck().getACard(i).setValue(10);
            } else {
                getDeck().getACard(i).setValue(11);
            }
        }
    }

    void setUpGame() {
        if (getCurrentRound() == 0) {
            db.createNewTable();
        }

        setCurrentRound(getCurrentRound()+1);
        setCardValues();
        getDeck().shuffle();
        for (Player player : getPlayers()) {
            getDeck().dealACard(player.getHand());
            getDeck().dealACard(player.getHand());
        }
        getDeck().dealACard(dealer.getHand());
        getDeck().dealACard(dealer.getHand());

        setCurrentPlayer(getPlayers()[roundsToPlay%getNumberOfHumanPlayers()]);
        addToListFromCurrentPlayer(getPlayers(), notFinishedPlayers, getCurrentPlayer());
    }

    void startTheGame() {
        setUpGame();
        while (notFinishedPlayers.size() > 0) {
            for (Player player : notFinishedPlayers) {
                currentSituation(player);
            }
            removePlayersFromList(notFinishedPlayers, listOfPlayersToRemove);
        }
        System.out.println("\nDealer's turn...\n");
        virtualPlayerMove(dealer);
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

    void currentSituation(Player player) {
        System.out.println(player.getName().toUpperCase() + " - IT'S YOUR TURN\n");
        confirm();

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
            makeMove(player);
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

    void makeMove(Player player) {
        int choice = intInputWithCheck("Do you want a hit or do you want to stay? " +
                "[Press 1 or 2, and confirm with ENTER]\n" +
                "1 - Hit me! (Draw another card)\n" +
                "2 - I'm good - I'll stay (End your turn)", 1, 2);

        switch (choice) {
            case 1:
                getDeck().dealACard(player.getHand());
                break;
            case 2:
                System.out.println("You've finished with " + player.getHand().getPoints() + " points.\n");
                confirm();
                listOfPlayersToRemove.add(player);
                break;
            default:
                break;
        }
    }

    void virtualPlayerMove(Player player) {
        confirm();
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
            virtualPlayerMove(player);
        } else {
            System.out.println("Dealer ends game with " + player.getHand().getPoints() + " points");
            printResults();
        }
    }

    void printResults() {
        ArrayList<Player> winners = new ArrayList<>();
        System.out.println("\nResults:\n");
        confirm();

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
            System.out.println("\n" + roundsToPlay + " rounds left...");

            resetSettings();
            confirm();
            startTheGame();
        } else {
            printFinalScore();
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

    void printFinalScore() {
        System.out.println("\nFinal result:");
        confirm();
        dealer.setPoints(dealer.getPoints() / getNumberOfHumanPlayers());

        class PointsComparator implements Comparator<Player> {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o2.getPoints(), o1.getPoints());
            }
        }
        Arrays.sort(getPlayers(), new PointsComparator());

        int scoreboardIndex = 1;
        for (Player player : getPlayers()) {
            System.out.println(scoreboardIndex + ". " + player.getName() + ": " + player.getPoints() + " points");
            scoreboardIndex++;
        }

        System.out.println("\nDealers points: " + dealer.getPoints());

        Player winner = getPlayers()[0];
        if (dealer.getPoints() > winner.getPoints()) {
            winner = dealer;
        }

        System.out.println("\nWINNER: " + winner.getName().toUpperCase() + "!!!");
    }
}
