package com.oneliferp.cwu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static final Pattern APPLY_PATTERN = Pattern.compile("([a-zA-Z]+(?: [a-zA-Z]+)?), #(\\d(?:\\d{4})?)", Pattern.MULTILINE);
    public static final Pattern EARNINGS_PATTERN = Pattern.compile("(\\d+)");

    public static Integer parseTokens(final String input) {
        try {
            final Matcher matcher = EARNINGS_PATTERN.matcher(input);
            if (!matcher.find()) return null;
            return Integer.parseInt(matcher.group());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
