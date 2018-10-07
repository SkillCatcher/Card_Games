package pl.skillcatcher.interfaces;

import org.junit.After;
import org.junit.Test;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

public class NameSetterTest implements NameSetter {

    @Test
    public void should_Set_Name() {
        ByteArrayInputStream in = new ByteArrayInputStream("Test name".getBytes());
        System.setIn(in);

        String testString = setNames(1)[0];
        assertEquals("Test name", testString);
    }

    @Test
    public void should_Set_Unknown() {
        ByteArrayInputStream in = new ByteArrayInputStream("    ".getBytes());
        System.setIn(in);

        String testString = setNames(1)[0];
        assertEquals("Unknown", testString);
    }

    @After
    public void restoreStream() {
        System.setIn(System.in);
    }
}