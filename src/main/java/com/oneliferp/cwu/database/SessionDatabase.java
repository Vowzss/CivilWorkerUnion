package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.models.CwuModel;
import com.oneliferp.cwu.models.SessionModel;
import com.oneliferp.cwu.misc.SessionType;

import java.util.List;

public class SessionDatabase extends JsonDatabase<SessionModel> {
    private static SessionDatabase instance;
    public static SessionDatabase get() {
        if (instance == null) instance = new SessionDatabase();
        return instance;
    }

    private SessionDatabase() {
        super(new TypeReference<List<SessionModel>>() {}, "session_data.json");
    }

    public List<SessionModel> getSessionsByType(final SessionType type) {
        return this.objects.stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<SessionModel> getSessionsByCwu(final CwuModel cwu) {
        return this.objects.stream()
                .filter(o -> o.getManagerCid().equals(cwu.getCid()))
                .toList();
    }
}
