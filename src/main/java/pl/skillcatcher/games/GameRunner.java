package pl.skillcatcher.games;


public class GameRunner {
    public static void main(String[] args) {

//        System.out.println("Please insert the number of rounds you want to play");
//        Scanner scanner = new Scanner(System.in);
//        int rounds = scanner.nextInt();
//
//        BlackJack game = new BlackJack(rounds);
//        game.startTheGame();

        Hearts kierki = new Hearts(0);
        kierki.startTheGame();

    }
}

//TODO: After finishing all Blackjack an Hearts tests, move GameRunner to separate package

//TODO: Update Blackjack
