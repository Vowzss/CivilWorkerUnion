package com.oneliferp.cwu.cache;

import com.oneliferp.cwu.models.ReportModel;

public class ReportCache extends RuntimeCache<String, ReportModel> {
    private static ReportCache instance;
    public static ReportCache get() {
        if (instance == null) instance = new ReportCache();
        return instance;
    }
}
