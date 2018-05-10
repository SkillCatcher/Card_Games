package pl.skillcatcher.cards;

public class Card {

    private String name;
    private int value;
    private Enum<Number> number;
    private Enum<Color> color;

    public Card(Enum<Number> number, Enum<Color> color) {
        this.number = number;
        this.color = color;
        this.name = number.toString() + " of " + color.toString();
        this.value = 0;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public Enum<Number> getNumber() {
        return number;
    }

    public Enum<Color> getColor() {
        return color;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
