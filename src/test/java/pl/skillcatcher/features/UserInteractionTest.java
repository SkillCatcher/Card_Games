package pl.skillcatcher.features;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class UserInteractionTest extends UserInteraction {
    private final ByteArrayOutputStream changedOutput = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStream() {
        System.setOut(new PrintStream(changedOutput));
    }

    @Test
    public void should_Confirm() {
        System.setIn(new ByteArrayInputStream(" ".getBytes()));

        confirm();

        assertEquals("To continue, press Enter (or write anything in the console - it doesn't matter)",
                changedOutput.toString().trim().replace("\r",""));
    }

    @Test
    public void should_Accept_Correct_Input() {
        ByteArrayInputStream in = new ByteArrayInputStream("6".getBytes());
        System.setIn(in);

        int userInput = intInputWithCheck("Testing message", 5, 7);

        assertEquals("Testing message", changedOutput.toString().trim().replace("\r",""));
        assertEquals(6, userInput);
    }

    @Test(expected = AssertionError.class)
    public void should_Not_Accept_Incorrect_Integer() {
        ByteArrayInputStream in = new ByteArrayInputStream("-9".getBytes());
        System.setIn(in);

        intInputWithCheck("Testing message", -5, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_Not_Accept_Incorrect_Input_NotInteger() {
        ByteArrayInputStream in = new ByteArrayInputStream("Random String".getBytes());
        System.setIn(in);

        intInputWithCheck("Testing message", 5, 7);
    }

    @After
    public void restoreStream() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Override
    public int intInputWithCheck(String message, int min, int max) {
        System.out.println("\n" + message);
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();

            if (choice > max || choice < min) {
                System.out.println("\nIncorrect number - please choose a number between "
                        + min + " and " + max + ":");
                throw new AssertionError("Method calls itself here again...");
            } else {
                return choice;
            }
        } else {
            System.out.println("\nIncorrect input - please choose a NUMBER between "
                    + min + " and " + max + ":");
            throw new IllegalArgumentException("Method calls itself here again...");
        }
    }
}