package org.aju.tests;

import org.aju.annotations.Test;

public class StressTest1 {
    private static void longLoop() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void test1() {
        longLoop();
    }

    @Test
    public void test2() {
        longLoop();
    }

    @Test
    public void test3() {
        longLoop();
    }

    @Test
    public void test4() {
        longLoop();
    }

    @Test
    public void test5() {
        longLoop();
    }
}
