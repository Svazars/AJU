package org.aju.runners;

import org.aju.AJUException;
import org.aju.util.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.aju.util.Utils.mkString;

public final class TestClass {

    private Method setup;
    private Method tearDown;

    private final Class<?> target;
    private final Object instance;

    private static AJUException clashOfAnnotations(String className, String methodName, String a1, String a2) {
        return new AJUException(
                "Class's " + className + " method " + methodName + " is annotated both as " + a1 + " and as " + a2 + ".");
    }

    private Stream<Method> annots(Predicate<Method> p) {
        return Arrays.stream(target().getMethods()).filter(p);
    }

    public TestClass(Class<?> target) throws AJUException {
        this.target = target;

        final Constructor[] cs = target.getConstructors();
        if(cs.length != 1) {
            throw new AJUException("Class " + target.getName() + " does not have unique constructor.");
        }

        final Constructor c = cs[0];
        if(c.getParameterCount() != 0) {
            throw new AJUException("Class's " + target.getName() + " constructor should not receive parameters.");
        }

        try {
            instance = c.newInstance();
        } catch (Exception e) {
            throw new AJUException("Failed to run constructor " + c);
        }

        final List<Method> setups = annots(Utils::isSetup).collect(Collectors.toList());
        if(setups.size() > 1) {
            throw new AJUException("Class " + target.getName() + " contains more than one method annotated as @Setup\n"
                    + mkString(setups.stream().map(Method::toString), ",")
            );
        }

        setups.stream().findAny().ifPresent(s -> {
            if(Utils.isTest(s))     throw clashOfAnnotations(target.getName(), s.getName(), "@Test", "@Setup");
            if(Utils.isTearDown(s)) throw clashOfAnnotations(target.getName(), s.getName(), "@TearDown", "@Setup");
            assert setup() == null;
            setup = s;
        });

        final List<Method> tearDowns = annots(Utils::isTearDown).collect(Collectors.toList());
        if(tearDowns.size() > 1) {
            throw new AJUException("Class " + target.getName() + " contains more than one method annotated as @TearDown\n"
                    + mkString(tearDowns.stream().map(Method::toString), ","));
        }
        tearDowns.stream().findAny().ifPresent(s -> {
            if(Utils.isTest(s)) throw clashOfAnnotations(target.getName(), s.getName(), "@Test", "@TearDown");
            assert tearDown() == null;
            tearDown = s;
        });
    }

    public void runSetup() throws Exception {
        if (setup() != null) {
            setup().invoke(instance);
        }
    }

    public void runTearDown() throws Exception {
        if (tearDown() != null) {
            tearDown().invoke(instance);
        }
    }

    public Stream<Method> tests() {
        return annots(Utils::isTest);
    }

    public Object instance() {
        return instance;
    }

    public Method setup() {
        return setup;
    }

    public Method tearDown() {
        return tearDown;
    }

    public Class<?> target() {
        return target;
    }
}
