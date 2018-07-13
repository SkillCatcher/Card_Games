package pl.skillcatcher.games;

public class MainMenu implements CorrectIntInputCheck, Confirmable {

    private int gameChoice;

    public MainMenu() {
        System.out.println("Welcome! Today, all of these games are available:"
                + "\n\t1. BlackJack"
                + "\n\t2. Hearts"
                + "\nMore games in progress :)");
        this.gameChoice = intInputWithCheck("To choose your game, press it's number from the list " +
                "(or you can press 0 to quit)",
                0, 2);
        start(gameChoice);
    }

    private void start(int gameChoice) {
        switch (gameChoice) {
            case 0:
                System.out.println("Have a nice day :)");
                break;
            case 1:
                System.out.println("Let's play BlackJack!");
                confirm();
                BlackJack blackJack = new BlackJack();
                blackJack.startTheGame();
                break;
            case 2:
                System.out.println("Let's play Hearts!");
                confirm();
                Hearts hearts = new Hearts();
                hearts.startTheGame();
                break;
            default:
                break;
        }
    }
}
