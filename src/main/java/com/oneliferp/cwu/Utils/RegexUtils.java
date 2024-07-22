package com.oneliferp.cwu.Utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static final Pattern APPLY_PATTERN = Pattern.compile("([a-zA-Z]+(?: [a-zA-Z]+)?), #(\\d(?:\\d{4})?)", Pattern.MULTILINE);
    public static final Pattern EARNINGS_PATTERN = Pattern.compile("(\\d+)");

    public static HashMap<String, String> parseApply(final String input) {
        final HashMap<String, String> matches = new HashMap<>();
        final Matcher matcher = APPLY_PATTERN.matcher(input);
        while (matcher.find()) {
            matches.put(matcher.group(1), "#" + matcher.group(2));
        }
        return matches;
    }

    public static Integer parseEarnings(final String input) {
        final Matcher matcher = EARNINGS_PATTERN.matcher(input);
        if (!matcher.find()) return null;
        return Integer.parseInt(matcher.group());
    }
}
