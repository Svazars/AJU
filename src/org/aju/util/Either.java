package org.aju.util;

import java.util.stream.Stream;

public final class Either<K, T> {
    private final K left;
    private final T right;

    private Either(K k, T t) {
        left = k;
        right = t;
    }

    public static<K, T> Either left(K k) {
        return new Either<K, T>(k, null);
    }

    public static<K, T> Either right(T t) {
        return new Either<K, T>(null, t);
    }

    private boolean isLeft() {
        return left != null;
    }

    private boolean isRight() {
        return !isLeft();
    }

    public static <K, T> Stream<K> onlyLeft(Stream<Either<K, T>> s) {
        return s.filter(Either::isLeft).map(e -> e.left);
    }

    public static <K, T> Stream<T> onlyRight(Stream<Either<K, T>> s) {
        return s.filter(Either::isRight).map(e -> e.right);
    }
}