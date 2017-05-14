package org.aju.tests;

import org.aju.annotations.Setup;
import org.aju.annotations.Test;

public class ErrorInSetupTest {
    @Setup
    public static void beforeTest() throws Exception {
        throw new Exception();
    }

    @Test
    // Should not run tests in classes, which were not properly set up
    public static void shouldNotRunMe() {
    }
}
