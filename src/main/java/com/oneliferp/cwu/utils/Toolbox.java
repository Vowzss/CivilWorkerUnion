package com.oneliferp.cwu.utils;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

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

    public static <T> List<T> insert(final List<T> list, final int index, final T object) {
        final List<T> newList = new ArrayList<>(list);
        newList.add(index, object);
        return newList;
    }

    public static void setDefaulMenuOption(final StringSelectMenu.Builder menu, final List<SelectOption> options, final String value) {
        options.stream().filter(o -> o.getValue().equals(value))
                .findFirst()
                .ifPresent(menu::setDefaultOptions);
    }
}
