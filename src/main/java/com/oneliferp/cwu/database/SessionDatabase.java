package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.commands.session.models.SessionModel;
import com.oneliferp.cwu.commands.session.misc.SessionType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        this.readFromCache().forEach(session -> map.put(session.getId(), session));
    }

    @Override
    public void addOne(final SessionModel session) {
        this.map.put(session.getId(), session);
    }

    /* Utils */
    public List<SessionModel> getSessionsByType(final SessionType type) {
        return this.map.values().stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<SessionModel> getSessionsByCwu(final String cid) {
        return this.map.values().stream()
                .filter(o -> o.getEmployeeCid().equals(cid))
                .toList();
    }

    public List<SessionModel> getLatests(final int count) {
        return this.map.values().stream()
                .sorted(Comparator.comparing(v -> v.getPeriod().getEndedAt(), Comparator.reverseOrder()))
                .limit(count)
                .toList();
    }
}
