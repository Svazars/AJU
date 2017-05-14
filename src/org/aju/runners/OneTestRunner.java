package org.aju.runners;

import org.aju.util.Utils;
import org.aju.runners.reporting.Report;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class OneTestRunner {

    private final Consumer<String> log;
    private final Class<?> owner;
    private final Object instance;
    private final Method method;
    private final Class<?> expectedException;

    public OneTestRunner(Consumer<String> log, Class<?> owner, Object instance, Method m) {
        this.log = log;
        this.owner = owner;
        this.instance = instance;
        this.method = m;
        this.expectedException = Utils.expectedException(method);
    }

    public Report run() {
        log.accept("Running method " + method.getName());
        final long start = System.currentTimeMillis();

        try {
            method.invoke(instance);
            final long elapsed = System.currentTimeMillis() - start;
            return expectedException != null
                    ? Report.withError(owner, method, "Expected exception not thrown.")
                    : Report.successful(owner, method, elapsed);
        } catch (Throwable t) {
            final long elapsed = System.currentTimeMillis() - start;
            final Throwable e = tryGetCause(t);
            if (expectedException != null && expectedException.isAssignableFrom(e.getClass())) {
                return Report.successful(owner, method, elapsed);
            } else {
                return Report.withError(owner, method,
                        expectedException != null
                                ? "Expected exception <" + expectedException.getName() + ">" + " but received <" + e.getClass().getName() + ">."
                                : "Not expected exception <" + e.getClass().getName() + "> received.");
            }
        }
    }

    private static Throwable tryGetCause(Throwable t) {
        assert t != null;
        final Throwable cause = t.getCause();
        return cause != null ? cause : t;
    }
}
