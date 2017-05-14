package org.aju.tests;

import org.aju.annotations.Test;

public class ErrorInException {

    private static class DumbException extends Exception {}
    private static class DumberException extends DumbException {}

    @Test(expectedException = DumberException.class)
    public static void invalidExceptionType() throws Exception {
        throw new DumbException();
    }

    @Test(expectedException = DumberException.class)
    public static void noExceptionWhileExpectingIt() throws Exception {
    }

    @Test
    public static void exceptionWhileNotExpectingIt() throws Exception {
        throw new DumbException();
    }
}
