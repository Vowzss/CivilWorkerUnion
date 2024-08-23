package com.oneliferp.cwu.misc;

import com.oneliferp.cwu.utils.EmojiUtils;

public enum CwuBranch {
    CWU("Département Administratif", EmojiUtils.getScales()),
    DTL("Département Travail et Logement", EmojiUtils.getKey()),
    DMS("Département Médical et Scientifique", EmojiUtils.getHospital()),
    DRT("Département Restauration et Transport", EmojiUtils.getHamburger());

    private final String meaning;
    private final String emoji;

    CwuBranch(final String meaning, final String emoji) {
        this.meaning = meaning;
        this.emoji = emoji;
    }

    public String getMeaning() {
        return this.meaning;
    }

    public String getEmoji() {
        return this.emoji;
    }
}
