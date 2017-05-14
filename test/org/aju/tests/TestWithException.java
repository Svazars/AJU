package org.aju.tests;

import org.aju.annotations.Test;

public class TestWithException {

    private static class DumbException extends Exception {}
    private static class DumberException extends DumbException {}

    @Test(expectedException = DumbException.class)
    public static void catchesException() throws Exception {
        throw new DumberException();
    }
}
