package org.aju.util;

public final class Tuple<K, T> {
    public Tuple(K k, T t) {
        _1 = k;
        _2 = t;
    }

    public final K _1;
    public final T _2;
}
