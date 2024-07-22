package com.oneliferp.cwu.Utils;

import java.util.Map;
import java.util.stream.Collectors;

public class Toolbox {
    public static Integer tryParse(final String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String flatten(final Map<String, String> map) {
        return map.entrySet()
                .stream()
                .map(p -> String.format("%s %s", p.getKey(), p.getValue()))
                .collect(Collectors.joining("\n"));
    }
}
