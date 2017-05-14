package org.aju.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

    class None extends Throwable {}

    Class<? extends Throwable> expectedException() default None.class;
}