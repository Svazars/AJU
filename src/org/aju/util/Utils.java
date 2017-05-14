package org.aju.util;

import org.aju.annotations.Setup;
import org.aju.annotations.TearDown;
import org.aju.annotations.Test;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class Utils {

    public static String mkString(Stream<String> data, String separator) {
        return data.reduce("", (acc, r) -> acc + separator + r);
    }

    public static Class<?> expectedException(Method m) {
        final Test testAnnotation = m.getAnnotation(Test.class);
        if (testAnnotation != null && !testAnnotation.expectedException().equals(Test.None.class))
            return testAnnotation.expectedException();
        return null;
    }

    public static boolean isTest(Method m) {
        return m.getAnnotation(Test.class) != null;
    }

    public static boolean isSetup(Method m) {
        return m.getAnnotation(Setup.class) != null;
    }

    public static boolean isTearDown(Method m) {
        return m.getAnnotation(TearDown.class) != null;
    }


}
