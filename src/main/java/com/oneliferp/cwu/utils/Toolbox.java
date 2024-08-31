package com.oneliferp.cwu.utils;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.*;
import java.util.stream.Collectors;

public class Toolbox {
    public static <T> String flatten(final Collection<T> collection, final boolean newLine) {
        return collection.stream()
                .map(T::toString)
                .collect(Collectors.joining(newLine ? "\n" : " "));
    }

    public static <T> String flatten(final Collection<T> collection) {
        return flatten(collection, true);
    }

    public static <T> List<T> merge(final Collection<T> collection1, final Collection<T> collection2) {
        final List<T> collection = new ArrayList<>(collection1);
        collection.addAll(collection1);
        collection.addAll(collection2);
        return collection;
    }

    public static <A, B> Map<A, B> merge(final Map<A, B> map1, final Map<A, B> map2) {
        final Map<A, B> map = new HashMap<>();
        map.putAll(map1);
        map.putAll(map2);
        return map;
    }

    public static <T> List<T> insert(final List<T> list, final int index, final T object) {
        final List<T> newList = new ArrayList<>(list);
        newList.add(index, object);
        return newList;
    }

    public static <T> List<T> select(final List<T> list, final int start, final int end) {
        return list.subList(Math.max(0, start), Math.min(list.size(), end + 1));
    }

    public static void setDefaultMenuOption(final StringSelectMenu.Builder menu, final List<SelectOption> options, final String value) {
        options.stream().filter(o -> o.getValue().equals(value))
                .findFirst()
                .ifPresent(menu::setDefaultOptions);
    }

    public static double resolveDistribution(final Integer num1, final Integer num2) {
        return (double) num1 / num2 * 100;
    }
}
