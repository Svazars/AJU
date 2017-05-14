package org.aju.tests;

import org.aju.annotations.Setup;
import org.aju.annotations.TearDown;
import org.aju.annotations.Test;

import static org.aju.assertions.Assertions.assertNot;
import static org.aju.assertions.Assertions.assertThat;

public class TestWithPreparation {

    private static class Container {
        Container(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int x;
        private int y;
    }

    private static Container field1 = null;
    private static Container field2 = new Container(100, 200);

    @Setup
    public static void beforeTest() {
        assertThat(field1 == null);
        assertThat(field2.x == 100);
        assertThat(field2.y == 200);

        field1 = new Container(1, 200);
    }

    @Test
    public static void usePreparedValue() {
        assertNot(field1.x == field2.x);
        assertThat(field1.y == field2.y);
    }

    @TearDown
    public static void afterTest() {
        assertNot(field1 == null);
        assertNot(field2 == null);
    }
}
