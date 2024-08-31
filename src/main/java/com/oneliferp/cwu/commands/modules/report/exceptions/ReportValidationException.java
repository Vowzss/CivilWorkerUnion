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
        String sb = "La génération du rapport a échoué car des données sont manquantes." +
                    "\n\n" +
                    EmojiUtils.getAnnotationGuide();
        return sb;
    }
}
