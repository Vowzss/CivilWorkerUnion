package com.oneliferp.cwu.Cache;

import com.oneliferp.cwu.Models.SessionModel;

import java.util.HashMap;

public class ReportCache {
    private static ReportCache instance;

    public static ReportCache getInstance() {
        if (instance == null) instance = new ReportCache();
        return instance;
    }

    private final HashMap<Long, SessionModel> reportMap;

    public ReportCache() {
        this.reportMap = new HashMap<>();
    }

    public void add(final Long userID, final SessionModel report) {
        this.reportMap.put(userID, report);
    }

    public SessionModel get(final Long userID) {
        return this.reportMap.get(userID);
    }

    public void remove(final Long userID) {
        this.reportMap.remove(userID);
    }

    public boolean contains(final Long userID) {
        return this.reportMap.containsKey(userID);
    }
}
