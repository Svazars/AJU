package org.aju.runners.advanced;

import org.aju.Main;
import org.aju.runners.OneTestRunner;
import org.aju.runners.Runner;
import org.aju.runners.TestClass;
import org.aju.runners.reporting.Report;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class ParallelTestRunner extends Runner {
    private final Consumer<String> log;
    private final int threadsToUse;

    public ParallelTestRunner(Consumer<String> log, int threads) {
        this.log = log;
        this.threadsToUse = threads;
    }

    private static TestClass prepareClass(Class<?> target) {
        return new TestClass(target);
    }

    private static Collection<Class<?>> classes(Collection<Report> targets) {
        return targets.stream().map(Report::testOwner).collect(toList());
    }

    private static boolean hasSetup(TestClass tc) {
        return tc.setup() != null;
    }

    @Override
    public String test(Collection<Class<?>> targets) {
        final long start = System.currentTimeMillis();
        final Predicate<TestClass> hasSetup = ParallelTestRunner::hasSetup;

        final Collection<TestClass> targetsToTest = targets.stream()
                .distinct()
                .map(ParallelTestRunner::prepareClass)
                .collect(toList());

        // flat list of target tests
        // 1. add all tests from classes with empty @Setup method
        final Collection<OneTestRunner> primitiveTests = testList(
                targetsToTest.stream().filter(hasSetup.negate()));

        // call @Setup of all classes (single-threaded operation)
        final Collection<Report> setuped = targetsToTest.stream()
                .filter(hasSetup)
                .map(this::setupClass)
                .collect(toList());

        final Map<Boolean, List<Report>> groupedSetups = setuped.stream()
                .collect(Collectors.partitioningBy(Report::failed));

        final Collection<Class<?>> succeededTargets = classes(groupedSetups.get(false));
        final Collection<Class<?>> failedTargets = classes(groupedSetups.get(true));

        final Collection<OneTestRunner> testsFromSetuped = testList(
                targetsToTest.stream().filter(t -> succeededTargets.contains(t.target())));

        assert primitiveTests.stream().filter(testsFromSetuped::contains).count() == 0;
        // 2. add all tests from successfully initialized classes
        primitiveTests.addAll(testsFromSetuped);

        final Collection<Report> results = runTestMethodsParallel(primitiveTests);

        // call @TearDown's (single-threaded) only on classes that
        //  a) have @TearDown method
        //  b) not failed @Setup method (if any)
        final Collection<Report> tearDowns = targetsToTest.stream()
                .filter(t -> t.tearDown() != null)
                .filter(t -> !failedTargets.contains(t.target()))
                .map(this::tearDownClass)
                .collect(toList());

        final Collection<Report> reports = new ArrayList<>();
        reports.addAll(setuped);
        reports.addAll(results);
        reports.addAll(tearDowns);

        return formatStats(targets, reports, System.currentTimeMillis() - start);
    }

    private Collection<Report> runTestMethodsParallel(Collection<OneTestRunner> runners) {
        final ForkJoinPool forkJoinPool = new ForkJoinPool(threadsToUse);
        try {
            return CompletableFuture.supplyAsync(
                    () -> runners.stream().parallel().map(OneTestRunner::run).collect(toList()),
                    forkJoinPool
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Failed to run tests in parallel. Reason: " + e);
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    private Collection<OneTestRunner> testList(Stream<TestClass> targets) {
        return targets.flatMap(
                tc -> tc.tests().map(m -> new OneTestRunner(log, tc.target(), tc.instance(), m))
        ).collect(toList());
    }

    private Report setupClass(TestClass tc) {
        final Class<?> target = tc.target();
        assert tc.setup() != null;
        log.accept("Running setup of class " + target.getName());

        final long start = System.currentTimeMillis();
        try {
            tc.runSetup();
        } catch (Exception e) {
            return Report.withError(target, tc.setup(), "Failed to set up class " + target.getName() + ".");
        }

        return Report.successful(target, tc.setup(), System.currentTimeMillis() - start);
    }

    private Report tearDownClass(TestClass tc) {
        final Class<?> target = tc.target();
        assert tc.tearDown() != null;
        log.accept("Running tear down of class " + target.getName());

        final long start = System.currentTimeMillis();
        try {
            tc.runTearDown();
        } catch (Exception e) {
            return Report.withError(target, tc.tearDown(), "Failed to tear down class " + target.getName() + ".");
        }

        return Report.successful(target, tc.tearDown(), System.currentTimeMillis() - start);
    }
}
