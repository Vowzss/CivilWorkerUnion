package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.utils.SimpleDate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SessionDatabase extends JsonDatabase<String, SessionModel> {
    /* Singleton */
    private static SessionDatabase instance;
    private final List<Path> paths;
    private final File currFile;
    private SessionDatabase() {
        super(new TypeReference<>() {
        }, Path.of("persistence/sessions"));

        final LocalDate firstWeekDay = SimpleDate.getFirstWeekDay();
        final LocalDate lastWeekDay = SimpleDate.getLastWeekDay();

        final String fileName = firstWeekDay.format(SimpleDate.DATE_FORMATTER) + "_" + lastWeekDay.format(SimpleDate.DATE_FORMATTER) + ".json";
        this.currFile = Paths.get(fileName).toFile();

        try (var stream = Files.list(this.directory)) {
            this.paths = stream.map(s -> {
                final String fn = s.getFileName().toString();

                return s.getFileName();
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files in directory: " + this.directory, e);
        }
    }

    public static SessionDatabase get() {
        if (instance == null) instance = new SessionDatabase();
        return instance;
    }

    /* Helpers */
    public static int resolveEarnings(final Collection<SessionModel> collection) {
        if (collection.isEmpty()) return 0;
        return collection.stream()
                .mapToInt(s -> s.getIncome().getEarnings())
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

    public static List<SessionModel> resolveLatestLimit(final Collection<SessionModel> sessions, final int count) {
        return sessions.stream()
                .sorted(Comparator.comparing(v -> v.getPeriod().getEndedAt(), Comparator.reverseOrder()))
                .limit(count)
                .toList();
    }

    public static SessionModel resolveMostRecent(final Collection<SessionModel> sessions) {
        if (sessions.isEmpty()) return null;

        return sessions.stream()
                .sorted(Comparator.comparing(v -> v.getPeriod().getEndedAt(), Comparator.reverseOrder()))
                .findFirst().get();
    }

    /* Methods */
    @Override
    public void addOne(final SessionModel session) {
        System.out.printf("TYPE: '%s', IDENTITY: '%s'%n", session.getType().name(), session.getEmployee());
        this.map.put(session.getId(), session);
    }

    /* Persistence methods */
    @Override
    public void save() {
        this.writeToCache(this.map.values(), this.paths.get(0).toFile());
    }

    @Override
    public void clear() {
        this.clearCache(this.paths.get(0).toFile());
    }

    /* Utils */
    public List<SessionModel> resolveFromWeek(final int limit) {
        return this.map.values().stream().filter(SessionModel::isWithinWeek)
                .limit(limit)
                .toList();
    }

    public List<SessionModel> resolveFromWeek() {
        return this.map.values().stream().filter(SessionModel::isWithinWeek)
                .toList();
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

    public SessionModel resolveLatest() {
        if (this.map.values().isEmpty()) return null;

        return this.map.values().stream()
                .sorted(Comparator.comparing(v -> v.getPeriod().getEndedAt(), Comparator.reverseOrder()))
                .findFirst().get();
    }
}
