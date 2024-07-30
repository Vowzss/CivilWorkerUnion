package com.oneliferp.cwu.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class Environment {
    private static final Dotenv env = Dotenv.load();

    private static String get(final String key) { return env.get(key); }

    public static String getToken() { return get("TOKEN"); }
}
