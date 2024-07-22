package com.oneliferp.cwu.misc;

public enum CwuBranch {
    DTL("Département Travail et Logement"),
    DMS("Département Médical et Scientifique"),
    DRT("Département Restauration et Transport");

    private final String meaning;

    CwuBranch(final String meaning) {
        this.meaning = meaning;
    }

    public String getMeaning() {
        return this.meaning;
    }
}
