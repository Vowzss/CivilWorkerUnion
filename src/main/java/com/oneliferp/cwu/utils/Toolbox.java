package com.oneliferp.cwu.utils;

import java.util.*;
import java.util.stream.Collectors;

public class Toolbox {
    public static <T> String flatten(final Collection<T> collection) {
        return collection.stream()
                .map(T::toString)
                .collect(Collectors.joining("\n"));
    }
}
