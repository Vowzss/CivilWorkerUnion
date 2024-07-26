package com.oneliferp.cwu.Cache;

import com.oneliferp.cwu.Database.CwuDatabase;

public class CidCache extends RuntimeCache<Long, String> {
    private static CidCache instance;
    public static CidCache get() {
        if (instance == null) instance = new CidCache();
        return instance;
    }

    private CidCache () {
        CwuDatabase.get().getAll().forEach(cwu -> {
            if (!cwu.isLinked()) return;
            this.add(cwu.getId(), cwu.getCid());
        });
    }
}
