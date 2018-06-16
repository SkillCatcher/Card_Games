package pl.skillcatcher.cards;

import java.util.List;

public class Player {

    private String name;
    private int points;
    private Hand hand;
    private PlayerStatus playerStatus;
    private int id;

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public int getId() {
        return id;
    }

    public Player() {
        this.playerStatus = PlayerStatus.NOT_ACTIVE;
    }

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.points = 0;
        this.hand = new Hand();
        this.playerStatus = PlayerStatus.USER;
    }

    public Player(String name, int id, PlayerStatus playerStatus) {
        this.name = name;
        this.id = id;
        this.points = 0;
        this.hand = new Hand();
        this.playerStatus = playerStatus;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public Card getCard(int index) {
        return getHand().getACard(index);
    }

    public List<Card> getCards() {
        return getHand().getCards();
    }

}
