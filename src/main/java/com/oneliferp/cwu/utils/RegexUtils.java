package com.oneliferp.cwu.utils;

import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.exceptions.IdentityMalformedException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static final Pattern APPLY_PATTERN = Pattern.compile("([a-zA-Z]+(?: [a-zA-Z]+)?), #(\\d(?:\\d{4})?)", Pattern.MULTILINE);
    public static final Pattern EARNINGS_PATTERN = Pattern.compile("(\\d+)");

    public static List<IdentityModel> parseIdentities(final String identities) throws IdentityMalformedException {
        final List<IdentityModel> list = new ArrayList<>();
        final Matcher matcher = APPLY_PATTERN.matcher(identities);

        matcher.results().forEach(rs -> {
            final String[] names = matcher.group(1).split(" ");

            // This parameter can be optional depending on the case
            final String lastName = names.length > 1 ? (names[1] != null ? names[1] : "") : "";
            list.add(new IdentityModel(names[0], lastName, matcher.group(2)));
        });

        if (list.isEmpty()) throw new IdentityMalformedException();
        return list;
    }

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
