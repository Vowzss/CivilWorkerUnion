package com.oneliferp.cwu.utils;

import java.util.*;
import java.util.stream.Collectors;

public class Toolbox {
    public static <T> String flatten(final Collection<T> collection) {
        return collection.stream()
                .map(T::toString)
                .collect(Collectors.joining("\n"));
    }

    public static <T> List<T> merge(final Collection<T> collection1, final Collection<T> collection2) {
        final List<T> collection = new ArrayList<>();
        collection.addAll(collection1);
        collection.addAll(collection2);
        return collection;
    }
}
