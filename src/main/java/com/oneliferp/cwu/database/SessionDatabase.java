package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.modules.session.models.SessionModel;
import com.oneliferp.cwu.modules.session.misc.SessionType;

import java.util.List;

public class SessionDatabase extends JsonDatabase<String, SessionModel> {
    /* Singleton */
    private static SessionDatabase instance;
    public static SessionDatabase get() {
        if (instance == null) instance = new SessionDatabase();
        return instance;
    }

    private SessionDatabase() {
        super(new TypeReference<>() {
        }, "session_db.json");
    }

    @Override
    public void addOne(final SessionModel value) {
        this.map.put(value.getManagerCid(), value);
    }

    /* Utils */
    public List<SessionModel> getSessionsByType(final SessionType type) {
        return this.map.values().stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<SessionModel> getSessionsByCwu(final CwuModel cwu) {
        return this.map.values().stream()
                .filter(o -> o.getManagerCid().equals(cwu.getCid()))
                .toList();
    }
}
