package org.aju.assertions;

public class Assertions {

    public static void assertThat(boolean expression) {
        if(!expression) {
            throw new AJUAssertionFailed();
        }
    }

    public static void assertNot(boolean expression) {
        if(expression) {
            throw new AJUAssertionFailed();
        }
    }
}
