package com.oneliferp.cwu.cache;

import com.oneliferp.cwu.models.SessionModel;

public class SessionCache extends RuntimeCache<String, SessionModel> {
    private static SessionCache instance;
    public static SessionCache get() {
        if (instance == null) instance = new SessionCache();
        return instance;
    }
}
