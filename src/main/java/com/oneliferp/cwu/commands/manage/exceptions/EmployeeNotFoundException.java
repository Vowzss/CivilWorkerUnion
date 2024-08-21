package com.oneliferp.cwu.commands.manage.exceptions;

import com.oneliferp.cwu.exceptions.CwuException;

public class EmployeeNotFoundException extends CwuException {
    @Override
    public String getMessage() {
        return "Cet employé n'existe pas dans notre registre.";
    }
}
