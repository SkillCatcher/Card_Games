package pl.skillcatcher.games;

public class MainMenu implements CorrectIntInputCheck, Confirmable, NameSetter {

    public MainMenu() {
        System.out.println("Welcome! Today, all of these games are available:"
                + "\n\t1. BlackJack"
                + "\n\t2. Hearts"
                + "\nMore games in progress :)");
        start(intInputWithCheck("To choose your game, press it's number from the list " +
                        "(or you can press 0 to quit)",
                0, 2));
    }

    private void start(int gameChoice) {
        switch (gameChoice) {
            case 0:
                System.out.println("Have a nice day :)");
                break;
            case 1:
                System.out.println("Let's play BlackJack!");
                confirm();
                int numberOfPlayers = intInputWithCheck("Please choose a number of players " +
                        "(between 1 and 7):", 1, 7);
                int numberOfRounds = intInputWithCheck("Please choose a number of rounds you want to play " +
                        "(between 1 and 100):", 1, 100);

                BlackJack blackJack = new BlackJack(numberOfPlayers, numberOfRounds, setNames(numberOfPlayers));
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
