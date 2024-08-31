package com.oneliferp.cwu.misc;

import java.security.SecureRandom;

public class IdFactory {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 9;
    private static IdFactory instance;
    private final SecureRandom secureRandom = new SecureRandom();

    public static IdFactory get() {
        if (instance == null) instance = new IdFactory();
        return instance;
    }

    public String generateID() {
        final StringBuilder id = new StringBuilder(ID_LENGTH);

        for (int i = 0; i < ID_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }

        return id.toString();
    }
}
