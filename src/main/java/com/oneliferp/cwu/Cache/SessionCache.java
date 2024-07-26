package com.oneliferp.cwu.Cache;

import com.oneliferp.cwu.Models.SessionModel;

public class SessionCache extends RuntimeCache<String, SessionModel> {
    private static SessionCache instance;
    public static SessionCache get() {
        if (instance == null) instance = new SessionCache();
        return instance;
    }
}
