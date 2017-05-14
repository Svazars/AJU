package org.aju.tests;

import org.aju.annotations.Setup;
import org.aju.annotations.TearDown;
import org.aju.annotations.Test;

import static org.aju.assertions.Assertions.assertNot;
import static org.aju.assertions.Assertions.assertThat;

public class ErrorInTearDownTest {

    private static Object deletedBySetup = new Object();

    @Setup
    public static void beforeTest() {
        deletedBySetup = null;
    }

    @Test
    public static void test() {
        assertThat(deletedBySetup == null);
    }

    @TearDown
    public static void throwNPEInTearDown() {
        System.out.println(deletedBySetup.hashCode());
    }
}
