package com.oneliferp.cwu.Utils;

import com.oneliferp.cwu.exceptions.IdentityMalformedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static final Pattern APPLY_PATTERN = Pattern.compile("([a-zA-Z]+(?: [a-zA-Z]+)?), #(\\d(?:\\d{4})?)", Pattern.MULTILINE);
    public static final Pattern EARNINGS_PATTERN = Pattern.compile("(\\d+)");

    public static String[] parseIdentity(final String identity) throws IdentityMalformedException {
        final String[] parts = new String[3];

        final Matcher matcher = APPLY_PATTERN.matcher(identity);
        if (!matcher.find()) throw new IdentityMalformedException();

        final String[] names = matcher.group(1).split(" ");
        parts[0] = names[0];
        parts[1] = names[1];
        parts[2] = matcher.group(2);

        return parts;
    }

    public static List<String[]> parseIdentities(final String identities) throws IdentityMalformedException {
        final List<String[]> list = new ArrayList<>();

        final Matcher matcher = APPLY_PATTERN.matcher(identities);
        if (!matcher.find()) throw new IdentityMalformedException();

       while (matcher.find()) {
           final String[] parts = new String[3];

           final String[] names = matcher.group(1).split(" ");
           parts[0] = names[0];
           parts[1] = names[1];
           parts[2] = matcher.group(2);

           list.add(parts);
       }

        return list;
    }

    public static Integer parseEarnings(final String input) {
        final Matcher matcher = EARNINGS_PATTERN.matcher(input);
        if (!matcher.find()) return null;
        return Integer.parseInt(matcher.group());
    }
}
