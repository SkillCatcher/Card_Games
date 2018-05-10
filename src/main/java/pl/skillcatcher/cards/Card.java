package pl.skillcatcher.cards;

public class Card {

    private String name;
    private int value;
    private int id;

    public Card(int id) {
        this.name = determineNumber(id) + " of " + determineColor(id);
        this.value = 0;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private String determineNumber(int i) {
        switch(i/4) {
            case 12:
                return "Ace";
            case 0:
                return "Two";
            case 1:
                return "Three";
            case 2:
                return "Four";
            case 3:
                return "Five";
            case 4:
                return "Six";
            case 5:
                return "Seven";
            case 6:
                return "Eight";
            case 7:
                return "Nine";
            case 8:
                return "Ten";
            case 9:
                return "Jack";
            case 10:
                return "Queen";
            case 11:
                return "King";
            default:
                return "Error";
        }
    }

    private String determineColor(int i) {
        switch (i%4) {
            case 0:
                return "Clubs";
            case 1:
                return "Diamonds";
            case 2:
                return "Hearts";
            case 3:
                return "Spades";
            default:
                return "Error";
        }
    }
}
