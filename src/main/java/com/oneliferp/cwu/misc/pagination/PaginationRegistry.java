package com.oneliferp.cwu.misc.pagination;

import com.oneliferp.cwu.commands.modules.profile.misc.ProfilePageType;
import com.oneliferp.cwu.commands.modules.report.misc.ReportType;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportPageType;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;
import com.oneliferp.cwu.utils.Toolbox;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PaginationRegistry {
    private static final Map<ReportType, List<ReportPageType>> reportPages = fillReportPages();
    private static final Map<SessionType, List<SessionPageType>> sessionPages = fillSessionPages();
    private static final List<ProfilePageType> employeePages = List.of(ProfilePageType.IDENTITY, ProfilePageType.ID, ProfilePageType.BRANCH, ProfilePageType.RANK, ProfilePageType.JOINED_AT);

    public static List<ReportPageType> getReportPages(final ReportType type) {
        return reportPages.getOrDefault(type, Collections.emptyList());
    }

    public static List<SessionPageType> getSessionPages(final SessionType type) {
        return sessionPages.getOrDefault(type, Collections.emptyList());
    }

    public static List<ProfilePageType> getProfilePages() {
        return employeePages;
    }

    /* Methods */
    private static Map<ReportType, List<ReportPageType>> fillReportPages() {
        final Map<ReportType, List<ReportPageType>> map = new EnumMap<>(ReportType.class);

        map.put(ReportType.STOCK, List.of(ReportPageType.STOCK, ReportPageType.COST, ReportPageType.INFO));

        map.put(ReportType.HOUSING_ATTRIBUTION, List.of(ReportPageType.TENANT, ReportPageType.RENT, ReportPageType.INFO));
        map.put(ReportType.HOUSING_PAYMENT, List.of(ReportPageType.TENANT, ReportPageType.RENT, ReportPageType.INFO));
        map.put(ReportType.HOUSING_EVICTION, List.of(ReportPageType.TENANT, ReportPageType.INFO));
        map.put(ReportType.HOUSING_CLEANING, List.of(ReportPageType.HEALTHINESS, ReportPageType.INFO));

        map.put(ReportType.BUSINESS_ATTRIBUTION, List.of(ReportPageType.MERCHANT, ReportPageType.RENT, ReportPageType.INFO));
        map.put(ReportType.BUSINESS_PAYMENT, List.of(ReportPageType.MERCHANT, ReportPageType.RENT, ReportPageType.INFO));
        map.put(ReportType.BUSINESS_ORDER, List.of(ReportPageType.MERCHANT, ReportPageType.COST, ReportPageType.TAX, ReportPageType.INFO));
        map.put(ReportType.BUSINESS_CLEANING, List.of(ReportPageType.HEALTHINESS, ReportPageType.INFO));

        map.put(ReportType.MEDICAL_INTERVENTION, List.of(ReportPageType.PATIENT, ReportPageType.MEDICAL, ReportPageType.INFO));
        map.put(ReportType.MEDICAL_ORDER, List.of(ReportPageType.COST, ReportPageType.TAX, ReportPageType.INFO));

        map.put(ReportType.OTHER, List.of(ReportPageType.INFO));

        map.replaceAll((k, v) -> v == null ? null : Collections.unmodifiableList(v));

        return Collections.unmodifiableMap(map);
    }

    /* Methods */
    private static Map<SessionType, List<SessionPageType>> fillSessionPages() {
        final Map<SessionType, List<SessionPageType>> map = new EnumMap<>(SessionType.class);

        final var defaults = SessionType.getDefaultPages();
        final var earningsPages = Toolbox.insert(defaults, defaults.size() - 1, SessionPageType.TOKENS);
        final var zonePages = Toolbox.insert(defaults, 0, SessionPageType.ZONE);

        map.put(SessionType.RATION, earningsPages);
        map.put(SessionType.CAN, earningsPages);
        map.put(SessionType.LAUNDRY, earningsPages);
        map.put(SessionType.CARDBOARD, earningsPages);
        map.put(SessionType.PRINTING, SessionType.getDefaultPages());
        map.put(SessionType.CLEANING, zonePages);
        map.put(SessionType.RENOVATION, zonePages);
        map.put(SessionType.GARDENING, zonePages);
        map.put(SessionType.OTHER, zonePages);

        map.replaceAll((k, v) -> v == null ? null : Collections.unmodifiableList(v));

        return Collections.unmodifiableMap(map);
    }
}
