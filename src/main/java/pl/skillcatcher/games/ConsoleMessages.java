package pl.skillcatcher.games;

public class ConsoleMessages {

    public ConsoleMessages() {
    }

    public final String YOUR_HAND = "Your hand:";
    public final String CARD_PICK = "Pick a card: ";
    public final String CARD_PICK_FAIL = "Sorry - you can't play that card, because:";
    public final String CARD_PICK_FAIL_COLOR_RULE = "- card's colour doesn't match with color of the first card";
    public final String NEW_CHOICE = "\nPlease choose again...";
    public final String END_OF_DEAL = "\nEnd of deal";
    public final String CARDS_IN_GAME = "\nCards in game:";
    public final String POOL_WINNER = "This pool goes to %s";
    public final String SCOREBOARD = "Scoreboard";
    public final String SCOREBOARD_ROW = "%s: %d points";
    public final String SCOREBOARD_ROW_WITH_NUM = "%d. " + SCOREBOARD_ROW;
    public final String SCOREBOARD_LEADER = "\nWinner - %s";
    public final String TURN_START = "%s - IT'S YOUR TURN \nOther players - no peeking :)\n";

    public final String MENU_TO_BE_CONTINUED = "More games available in the future...";
    public final String MENU_WELCOME_MESSAGE = "Welcome! Today, all of these games are available:"
            + "\n\t1. BlackJack"
            + "\n\t2. Hearts"
            + "\nMore games in progress :)";
    public final String MENU_GAME_CHOICE = "To choose your game, press it's number from the list " +
            "(or you can press 0 to quit)";
    public final String MENU_QUIT = "Have a nice day :)";
    public final String MENU_BLACKJACK_START = "Let's play BlackJack!";
    public final String MENU_BLACKJACK_PLAYERS_NUM = "Please choose a number of players (between 1 and 7):";
    public final String MENU_BLACKJACK_ROUNDS_NUM = "Please choose a number of rounds you want to play (between 1 and 100):";
    public final String MENU_HEARTS_START = "Let's play Hearts!";
    public final String MENU_HEARTS_PLAYERS_NUM = "Please choose the number of HUMAN players " +
            "- between 0 (if you just want to watch and press enter) and 4 (all human players):";

    public final String BLACKJACK_DEALER_CARD = "You can see, that dealer's got a %s.\n";
    public final String BLACKJACK_OVER_21 = "Oops... your currently have %d points - it's more than 21. You've lost...";
    public final String BLACKJACK_OTHER_HAND = "Hand of %s:";
    public final String BLACKJACK_DECISION = "Do you want a hit or do you want to stay? " +
            "[Press 1 or 2, and confirm with ENTER]\n" +
            "1 - Hit me! (Draw another card)\n" +
            "2 - I'm good - I'll stay (End your turn)";
    public final String BLACKJACK_STAY = "You've finished with %d points.\n";
    public final String BLACKJACK_DEALER_HAND = "\nDealer currently has this hand: ";
    public final String BLACKJACK_DEALER_DRAW = "Dealer draws another card...";
    public final String BLACKJACK_DEALER_STAY = "Dealer ends game with %d points";
    public final String BLACKJACK_RESULTS = "\nResults:\n"; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! RE-USE
    public final String BLACKJACK_ROUND_WINNERS = "\nWinners of this round:";
    public final String BLACKJACK_RESET = "\n%d rounds left...";
    public final String BLACKJACK_FINAL_RESULTS = "\nFinal result:"; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! RE-USE
    public final String BLACKJACK_WINNER = "\nWINNER: %s!!!";
    public final String BLACKJACK_DEALER_FINAL_POINTS = "\nDealers points: %d";

    public final String HEARTS_PASSING_START = "Card Pass Turn for player: %s";
    public final String HEARTS_CHOOSE_3_CARDS = "Choose 3 cards from the list by their number (if you want to " +
            "reverse the pick, simply pick the same card again). They'll be passed to %s: \n";
    public final String HEARTS_3_CHOSEN_CARDS = "You've chosen: ";
    public final String HEARTS_PARTIAL_CHOICE = "Your choices so far...";
    public final String HEARTS_CHOOSE_A_CARD = "Choose card number %d:";
    public final String HEARTS_CHOICE_CANCELLED = "Choice of %s was cancelled.";
    public final String HEARTS_CARDS_ON_TABLE = "Cards in the game so far:";
    public final String HEARTS_ALLOWED_RULE = "- hearts still aren't allowed";
    public final String HEARTS_FIRST_DEAL_RULE = "- it's first deal: "
            + "\n\ta) it has to start with Two of Clubs"
            + "\n\tb) cards with points are not allowed";

    public final String UI_CONFIRM = "To continue, press Enter (or write anything in the console - it doesn't matter)";
    public final String UI_WRONG_NUMBER = "\nIncorrect number - please choose a number between %d and %d:";
    public final String UI_NOT_NUMBER = "\nIncorrect input - please choose a NUMBER between %d and %d:";
}