package org.aju.tests;

import org.aju.annotations.Test;

import static org.aju.assertions.Assertions.assertNot;
import static org.aju.assertions.Assertions.assertThat;

public class ErrorInSignature {

    @Test
    public static void callWithParams(Object t) {
        assertNot(1 == 2);
        assertThat(true);
    }

    @Test
    // Should not run inaccessible methods
    private static void shouldNotCallPrivateCall() {
        assertNot(1 == 2);
        assertThat(true);
    }
}
