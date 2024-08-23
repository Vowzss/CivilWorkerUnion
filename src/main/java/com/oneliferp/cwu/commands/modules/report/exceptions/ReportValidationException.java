package com.oneliferp.cwu.commands.modules.report.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import com.oneliferp.cwu.utils.EmojiUtils;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class ReportValidationException extends CwuException {
    public ReportValidationException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder();
        sb.append("La génération du rapport a échoué car des données sont manquantes.");
        sb.append("\n\n");
        sb.append(EmojiUtils.getAnnotationGuide());
        return sb.toString();
    }
}
