package org.aju.runners.reporting;

import java.lang.reflect.Method;

public abstract class Report {

    public abstract boolean failed();
    private final Class<?> testOwner;
    final Method method;

    private Report(Class<?> testOwner, Method method) {
        assert testOwner != null;
        assert method != null;
        this.testOwner = testOwner;
        this.method = method;
    }

    public Class<?> testOwner() {
        return testOwner;
    }

    private static final class Successful extends Report {
        private final long timeElapsed;

        Successful(Class<?> testOwner, Method method, long timeElapsed) {
            super(testOwner, method);
            this.timeElapsed = timeElapsed;
        }

        @Override
        public boolean failed() {
            return false;
        }

        public String toString() {
            return method.getName() + " OK. " + timeElapsed + " ms";
        }
    }

    private static final class Failed extends Report {
        private final String reason;

        Failed (Class<?> testOwner, Method method, String reason) {
            super(testOwner, method);
            this.reason = reason;
        }

        @Override
        public boolean failed() {
            return true;
        }

        public String toString() {
            return method.getName() + " FAILED. Reason: " + reason;
        }
    }

    public static Report successful(Class<?> owner, Method m, long timeElapsed) {
        return new Successful(owner, m, timeElapsed);
    }

    public static Report withError(Class<?> owner, Method m, String msg) {
        return new Failed(owner, m, msg);
    }
}
