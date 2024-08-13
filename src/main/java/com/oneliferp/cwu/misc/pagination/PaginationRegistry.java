package com.oneliferp.cwu.misc.pagination;

import com.oneliferp.cwu.modules.report.misc.ReportPageType;
import com.oneliferp.cwu.modules.report.misc.ReportType;
import com.oneliferp.cwu.modules.session.misc.SessionPageType;
import com.oneliferp.cwu.modules.session.misc.SessionType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PaginationRegistry {
    private static final Map<ReportType, List<ReportPageType>> reportPages = fillReportPages();
    public static List<ReportPageType> getReportPages(final ReportType type) {
        return reportPages.getOrDefault(type, Collections.emptyList());
    }

    private static final Map<SessionType, List<SessionPageType>> sessionPages = fillSessionPages();
    public static List<SessionPageType> getSessionPages(final SessionType type) {
        return sessionPages.getOrDefault(type, Collections.emptyList());
    }

    /* Methods */
    private static Map<ReportType, List<ReportPageType>> fillReportPages() {
        final Map<ReportType, List<ReportPageType>> map = new EnumMap<>(ReportType.class);

        map.put(ReportType.STOCK, List.of(ReportPageType.STOCK, ReportPageType.TOKENS, ReportPageType.INFO));
        map.put(ReportType.HOUSING_ATTRIBUTION, List.of(ReportPageType.IDENTITY, ReportPageType.TOKENS, ReportPageType.INFO));
        map.put(ReportType.HOUSING_PAYMENT, List.of(ReportPageType.IDENTITY, ReportPageType.TOKENS, ReportPageType.INFO));
        map.put(ReportType.HOUSING_EVICTION, List.of(ReportPageType.IDENTITY, ReportPageType.INFO));
        map.put(ReportType.MEDICAL_CHECK, null);
        map.put(ReportType.HOUSING_VERIFICATION, List.of(ReportPageType.INFO));
        map.put(ReportType.OTHER, List.of(ReportPageType.INFO));

        map.replaceAll((k, v) -> v == null ? null : Collections.unmodifiableList(v));

        return Collections.unmodifiableMap(map);
    }

    /* Methods */
    private static Map<SessionType, List<SessionPageType>> fillSessionPages() {
        final Map<SessionType, List<SessionPageType>> map = new EnumMap<>(SessionType.class);

        final var defaults = SessionType.getDefaultPages();
        final var earningsPages = Stream.concat(defaults.stream(), Stream.of(SessionPageType.TOKENS)).toList();
        final var zonePages = Stream.concat(Stream.of(SessionPageType.ZONE), defaults.stream()).toList();

        map.put(SessionType.RATION, earningsPages);
        map.put(SessionType.DISTILLERY, earningsPages);
        map.put(SessionType.LAUNDRY, earningsPages);
        map.put(SessionType.RECYCLING, earningsPages);
        map.put(SessionType.PRINTING, SessionType.getDefaultPages());
        map.put(SessionType.CLEANING, zonePages);
        map.put(SessionType.RENOVATION, zonePages);
        map.put(SessionType.GARDENING, zonePages);
        map.put(SessionType.OTHER, zonePages);

        map.replaceAll((k, v) -> v == null ? null : Collections.unmodifiableList(v));

        return Collections.unmodifiableMap(map);
    }
}
