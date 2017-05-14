package org.aju.runners;

import org.aju.util.Utils;
import org.aju.runners.reporting.Report;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class Runner {
    public abstract String test(Collection<Class<?>> targets);

    private static String format(Collection<Report> reports, Predicate<Report> p) {
        return Utils.mkString(reports.stream().filter(p).map(Runner::formatOneTest), "\n");
    }

    private static String formatOneTest(Report r) {
        return "  " + r.toString();
    }

    protected static String formatStats(Collection<Class<?>> targets, Collection<Report> reports, long elapsed) {
        final StringBuilder formatted = new StringBuilder();
        targets.forEach(t -> formatted.append("\n")
                .append(t.getName())
                .append(format(reports, r -> r.testOwner().equals(t) && !r.failed()))
                .append(format(reports, r -> r.testOwner().equals(t) && r.failed())));

        formatted.append("\nElapsed time = ")
                .append(elapsed)
                .append("ms");
        return formatted.toString();
    }
}
