package com.oneliferp.cwu.commands.session.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.utils.EmojiUtils;

public class SessionValidationException extends CwuException {
    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder();
        sb.append("L'enregistrement de la session a échoué car des données sont manquantes.");
        sb.append("\n\n");
        sb.append("**Détails des annotations:**\n");
        sb.append(String.format("%s  le champ est optionnel ou déjà rempli.\n", EmojiUtils.getGreenCircle()));
        sb.append(String.format("%s  un des champs annoté doit être défini\n", EmojiUtils.getYellowCircle()));
        sb.append(String.format("%s  le champ annoté doit être spécifié\n", EmojiUtils.getRedCircle()));
        return sb.toString();
    }
}
