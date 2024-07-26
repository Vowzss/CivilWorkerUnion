package com.oneliferp.cwu.Database;

import com.oneliferp.cwu.Models.CwuModel;
import com.oneliferp.cwu.Models.SessionModel;
import com.oneliferp.cwu.misc.SessionType;

import java.util.List;

public class SessionDatabase extends JsonDatabase<SessionModel> {
    private static SessionDatabase instance;
    public static SessionDatabase get() {
        if (instance == null) instance = new SessionDatabase();
        return instance;
    }

    private SessionDatabase() {
        super(SessionModel.class, "session_data.json");
    }

    public List<SessionModel> getSessionsByType(final SessionType type) {
        return this.objects.stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<SessionModel> getSessionsByCwu(final CwuModel cwu) {
        return this.objects.stream()
                .filter(o -> o.getCwuIdentity().equals(cwu.getIdentity()))
                .toList();
    }
}
