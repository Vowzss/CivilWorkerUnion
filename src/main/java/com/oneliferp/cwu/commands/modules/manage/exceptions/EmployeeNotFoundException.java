package com.oneliferp.cwu.commands.modules.manage.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class EmployeeNotFoundException extends CwuException {
    public EmployeeNotFoundException(final IReplyCallback callback) {
        super(callback);
    }

    @Override
    public String getMessage() {
        return "Cet employé n'existe pas dans notre registre.";
    }
}
