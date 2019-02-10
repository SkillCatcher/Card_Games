package pl.skillcatcher.games;

import pl.skillcatcher.features.Player;

import java.util.ArrayList;

public class PlayersInGame {

    private ArrayList<Player> notFinishedPlayers;
    private ArrayList<Player> listOfPlayersToRemove;

    public ArrayList<Player> getNotFinishedPlayers() {
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

    public PlayersInGame() {
        this.notFinishedPlayers = new ArrayList<>();
        this.listOfPlayersToRemove = new ArrayList<>();
    }

    public void removePlayersFromList() {
        for (Player player : listOfPlayersToRemove) {
            notFinishedPlayers.remove(player);
        }
    }

    public void addToRemove(Player player) {
        listOfPlayersToRemove.add(player);
    }

    public void clear() {
        notFinishedPlayers.clear();
        listOfPlayersToRemove.clear();
    }

    public void fillInPlayersInGameList(Player[] allPlayers, Player current) {
        for (int i = current.getId(); i < allPlayers.length + current.getId(); i++) {
            notFinishedPlayers.add(allPlayers[i % allPlayers.length]);
        }
    }
}
