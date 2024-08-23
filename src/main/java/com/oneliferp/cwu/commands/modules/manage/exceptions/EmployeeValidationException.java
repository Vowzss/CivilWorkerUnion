package com.oneliferp.cwu.commands.modules.manage.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.utils.EmojiUtils;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class EmployeeValidationException extends CwuException {
    public EmployeeValidationException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder();
        sb.append("La création de l'employé a échoué car des données sont manquantes.");
        sb.append("\n\n");
        sb.append(EmojiUtils.getAnnotationGuide());
        return sb.toString();
    }
}
