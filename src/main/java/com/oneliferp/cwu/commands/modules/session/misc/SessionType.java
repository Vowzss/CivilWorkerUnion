package com.oneliferp.cwu.commands.modules.session.misc;

import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;
import com.oneliferp.cwu.utils.EmojiUtils;
import net.fellbaum.jemoji.EmojiManager;

import java.util.List;

public enum SessionType {
    UNKNOWN("Inconnu", null),
    RATION("Ration", "\uD83E\uDD61"),
    CAN("Canette", "\uD83E\uDD64"),
    LAUNDRY("Laverie", "\uD83E\uDDFC"),
    CARDBOARD("Carton", EmojiManager.getByAlias(":recycle:").get().getEmoji()),
    PRINTING("Imprimerie", "\uD83D\uDDA8"),
    CLEANING("Nettoyage", "\uD83E\uDDF9"),
    RENOVATION("Rénovation", "\uD83E\uDDF1"),
    GARDENING("Jardinage", "\uD83C\uDF3F"),
    OTHER("Autre", EmojiUtils.getPencilMemo());

    private final String label;
    private final String emoji;

    SessionType(final String label, final String emoji) {
        this.label = label;
        this.emoji = emoji;
    }

    public static List<SessionPageType> getDefaultPages() {
        return List.of(SessionPageType.LOYALISTS, SessionPageType.CIVILIANS, SessionPageType.VORTIGAUNTS, SessionPageType.ANTI_CITIZENS, SessionPageType.INFO);
    }

    /* Getters */
    public String getLabel() {
        return this.label;
    }

    public String getEmoji() {
        return this.emoji;
    }

    /* Utils */
    public ZoneType getZone() {
        return switch (this) {
            default -> ZoneType.UNKNOWN;
            case RATION -> ZoneType.RATION_FACTORY;
            case CARDBOARD -> ZoneType.RECYCLING_CENTER;
            case CAN -> ZoneType.DISTILLERY;
            case LAUNDRY -> ZoneType.HANGAR_PLAZA;
            case PRINTING -> ZoneType.PRINTING_HOUSE;
        };
    }
}
