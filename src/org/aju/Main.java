package org.aju;

import org.aju.runners.advanced.ParallelTestRunner;
import org.aju.util.Tuple;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting AJU: Almost JUnit tool");

        final Main main = new Main();

        main.acceptOptions(args).forEach(arg -> {
            try {
                main.targets.add(getClass(arg));
            } catch (ClassNotFoundException e) {
                System.err.println("Could not find class [" + arg + "]:\n" + e);
            }
        });

        if(main.threadsToUse < 1) {
            System.out.println("--threads parameter is incorrect");
            System.exit(-1);
        }

        System.out.println("Run tests using " + main.threadsToUse + " threads");
        final ParallelTestRunner runner = new ParallelTestRunner(
                main.verboseLogs ? verboseLogger() : emptyLogger(),
                main.threadsToUse);
        try {
            final String stats = runner.test(main.targets);
            System.out.println("Testing stats:");
            System.out.println(stats);
        } catch (AJUException e) {
            System.out.println("Can not perform test:");
            e.printStackTrace(System.out);
        }
    }

    private Set<Class<?>> targets = new HashSet<>();
    private int threadsToUse = Runtime.getRuntime().availableProcessors();
    private boolean verboseLogs = false;

    private static Tuple<String, String> keyValueSplit(String arg) {
        assert arg.contains("=");
        final String[] ps = arg.split("=");
        assert ps.length == 2 : "Option <" + arg + "> is invalid";
        return new Tuple<>(ps[0], ps[1]);
    }

    private static boolean hasParamPrefix(String str) {
        return str.startsWith("--");
    }

    private static void drop(String arg) {}

    private static Consumer<String> verboseLogger() {
        return System.out::println;
    }

    private static Consumer<String> emptyLogger() {
        return Main::drop;
    }

    private Stream<String> acceptOptions(String[] args) {
        final Predicate<String> isParam = Main::hasParamPrefix;
        Stream.of(args)
                .filter(isParam)
                .map(Main::keyValueSplit).forEach(kv -> {
                    if(kv._1.equals("--threads")) threadsToUse = Integer.parseInt(kv._2);
                    if(kv._1.equals("--verbose")) verboseLogs = true;
                }
        );

        return Stream.of(args).filter(isParam.negate());
    }

    private static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(className, true, currentThread().getContextClassLoader());
    }
}
