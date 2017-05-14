package org.aju.tests;

import org.aju.annotations.*;

import static org.aju.assertions.Assertions.assertNot;
import static org.aju.assertions.Assertions.assertThat;

public class SimpleTest {

    @Test
    public void instanceCall() {
        assertThat(1 == 1);
    }

    @Test
    public static void compareInts() {
        assertThat(1 == 1);
    }

    @Test
    public static void testAsserts() {
        assertNot(1 == 2);
        assertThat(true);
    }

    @Test
    public static void longOne() {
        for(int i = 0; i < 100000; i++) {
            assertThat(new Object().hashCode() <= Integer.MAX_VALUE);
        }
    }
}
