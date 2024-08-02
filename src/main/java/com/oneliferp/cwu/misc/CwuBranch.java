package com.oneliferp.cwu.misc;

public enum CwuBranch {
    DTL("Département Travail et Logement", "\uD83C\uDFD7"),
    DMS("Département Médical et Scientifique", "\uD83D\uDE91"),
    DRT("Département Restauration et Transport", "\uD83C\uDF7D");

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
