package com.oneliferp.cwu.misc;

import com.oneliferp.cwu.commands.modules.report.models.*;
import com.oneliferp.cwu.utils.EmojiUtils;

public enum CwuBranch {
    CWU("Département Administratif", EmojiUtils.getScales()),
    DTL("Département Travail et Logement", EmojiUtils.getKey()),
    DMS("Département Médical et Scientifique", EmojiUtils.getHospital()),
    DRT("Département Restauration et Transport", EmojiUtils.getHamburger());

    private final String meaning;
    private final String emoji;
    //private final Class<? extends ReportModel> type;

    CwuBranch(final String meaning, final String emoji/*, final Class<? extends ReportModel> type*/) {
        this.meaning = meaning;
        this.emoji = emoji;
        //this.type = type;
    }

    public String getMeaning() {
        return this.meaning;
    }

    public String getEmoji() {
        return this.emoji;
    }

    /*public Class<? extends ReportModel> getType() {
        return this.type;
    }*/
}
