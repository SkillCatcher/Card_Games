package pl.skillcatcher.features;

public class Card {

    private String name;
    private int value;
    private int id;
    private CardNumber number;
    private CardColour colour;

    public Card(int id) throws IllegalArgumentException {
        if (id < 52 && id > -1) {
            this.name = determineNumber(id) + " of " + determineColour(id);
            this.value = 0;
            this.id = id;
            setNumber(id);
            setColour(id);
        } else {
            throw new IllegalArgumentException();
        }
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

    public CardNumber getNumber() {
        return number;
    }

    public CardColour getColour() {
        return colour;
    }

    private void setNumber(int i) {
        switch(i/4) {
            case 0:
                number = CardNumber.TWO;
                break;
            case 1:
                number = CardNumber.THREE;
                break;
            case 2:
                number = CardNumber.FOUR;
                break;
            case 3:
                number = CardNumber.FIVE;
                break;
            case 4:
                number = CardNumber.SIX;
                break;
            case 5:
                number = CardNumber.SEVEN;
                break;
            case 6:
                number = CardNumber.EIGHT;
                break;
            case 7:
                number = CardNumber.NINE;
                break;
            case 8:
                number = CardNumber.TEN;
                break;
            case 9:
                number = CardNumber.JACK;
                break;
            case 10:
                number = CardNumber.QUEEN;
                break;
            case 11:
                number = CardNumber.KING;
                break;
            case 12:
                number = CardNumber.ACE;
                break;
            default:
                System.out.println("Error");
        }
    }

    private void setColour(int i) {
        switch (i%4) {
            case 0:
                colour = CardColour.CLUBS;
                break;
            case 1:
                colour = CardColour.DIAMONDS;
                break;
            case 2:
                colour = CardColour.HEARTS;
                break;
            case 3:
                colour = CardColour.SPADES;
                break;
            default:
                System.out.println("Error");
        }
    }

    public void setValue(int value) {
        this.value = value;
    }

    private String determineNumber(int i) {
        switch(i/4) {
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
            case 12:
                return "Ace";
            default:
                return "Error";
        }
    }

    private String determineColour(int i) {
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
