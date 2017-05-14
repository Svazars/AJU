package org.aju.tests;

import org.aju.annotations.Test;

import static org.aju.assertions.Assertions.assertThat;

public class InhertitanceTest {

    // it *must* be public in order to run tests
    public static class Base {
        public Base() {}
        @Test
        public void failingBaseTest() {
            assertThat(false);
        }

        @Test
        public void stableBaseTest() {
            assertThat(true);
        }
    }

    public static class Derived extends Base {
        public Derived() {super();}

        // http://stackoverflow.com/questions/10082619/how-do-java-method-annotations-work-in-conjunction-with-method-overriding
        @Test
        @Override
        public void failingBaseTest() {
            assertThat(true);
        }
    }
}
