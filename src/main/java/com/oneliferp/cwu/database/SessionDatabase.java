package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.misc.CwuBranch;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    }

    @Override
    public void addOne(final SessionModel session) {
        System.out.printf("TYPE: '%s', IDENTITY: '%s'%n", session.getType().name(), session.getEmployee());
        this.map.put(session.getId(), session);
    }

    /* Utils */
    public static int resolveEarnings(final Collection<SessionModel> collection) {
        if (collection.isEmpty()) return 0;
        return collection.stream()
                .mapToInt(s -> (int) Math.floor(s.getIncome().getEarnings() * s.getEmployee().rank.getSessionRoyalty()))
                .sum();
    }

    public static int resolvePoints(final Collection<SessionModel> collection) {
        if (collection == null) return 0;
        return collection.size() / 2;
    }

    public static int resolveWages(final Collection<SessionModel> collection) {
        return collection.stream()
                .mapToInt(s -> s.getIncome().getWages())
                .sum();
    }

    public List<SessionModel> resolveWithinWeek() {
        return this.map.values().stream().filter(SessionModel::isWithinWeek)
                .toList();
    }

    public List<SessionModel> resolveLatestLimit(final int count) {
        return this.map.values().stream()
                .sorted(Comparator.comparing(v -> v.getPeriod().getEndedAt(), Comparator.reverseOrder()))
                .limit(count)
                .toList();
    }

    public SessionModel resolveLatest() {
        if (this.map.values().isEmpty()) return null;

        return this.map.values().stream()
                .sorted(Comparator.comparing(v -> v.getPeriod().getEndedAt(), Comparator.reverseOrder()))
                .findFirst().get();
    }

    public List<SessionModel> resolveByType(final SessionType type) {
        return this.map.values().stream()
                .filter(o -> o.getType() == type)
                .toList();
    }

    public List<SessionModel> resolveByEmployee(final String cid) {
        return this.map.values().stream()
                .filter(o -> Objects.equals(o.getEmployeeCid(), cid))
                .toList();
    }
}
