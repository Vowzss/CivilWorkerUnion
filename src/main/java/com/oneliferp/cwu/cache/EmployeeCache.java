package com.oneliferp.cwu.cache;

import com.oneliferp.cwu.commands.modules.manage.models.EmployeeModel;

public class EmployeeCache extends RuntimeCache<String, EmployeeModel> {
    private static EmployeeCache instance;
    public static EmployeeCache get() {
        if (instance == null) instance = new EmployeeCache();
        return instance;
    }
}
