package com.oneliferp.cwu.commands;

import com.oneliferp.cwu.misc.IButtonType;
import com.oneliferp.cwu.modules.profile.misc.ids.ProfileButtonType;
import com.oneliferp.cwu.modules.report.misc.ids.ReportButtonType;
import com.oneliferp.cwu.modules.report.misc.ids.ReportMenuType;
import com.oneliferp.cwu.modules.report.misc.ids.ReportModalType;
import com.oneliferp.cwu.modules.session.misc.ids.SessionButtonType;
import com.oneliferp.cwu.modules.session.misc.ids.SessionMenuType;
import com.oneliferp.cwu.modules.session.misc.ids.SessionModalType;
import com.oneliferp.cwu.modules.session.misc.SessionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContext {
    public final String type;
    public final String identifier;
    public final List<String> specifiers;
    public final HashMap<String, String> params;

    public final IButtonType enumType;

    public CommandContext(final String input) {
        final String[] parts = input.split("#", 2);
        this.type = parts[0];

        final String[] idAndRest = parts[1].split("/", 2);
        this.identifier = idAndRest[0];

        final String[] specifiersAndParams = idAndRest[1].split("\\?", 2);
        this.specifiers = Arrays.asList(specifiersAndParams[0].split("/"));

        this.params = new HashMap<>();
        Arrays.stream(specifiersAndParams[1].split("&")).forEach(param -> {
            final var pair = param.split("=", 2);
            this.params.put(pair[0], pair[1]);
        });

        this.enumType = resolveEnumType();

        System.out.printf("""
                Type: %s
                Identifier: %s
                Specifiers: %s
                Params: %s
                %n""", this.type, this.identifier, this.specifiers, this.params);
    }

    public String getCid() {
        return this.getParam("cid");
    }

    public SessionType getSession() {
        return SessionType.valueOf(this.getParam("session"));
    }

    public String getRoot() {
        return String.format("%s#%s", this.type, this.identifier);
    }

    public String getParam(final String key) {
        return this.params.get(key);
    }

    public String getCommand() {
        return this.identifier.split("_")[1];
    }

    /*
    Utils
    */
    public static String buildPattern(final String root, final String identifier, final String cid) {
        return String.format("%s/%s?cid=%s", root, identifier, cid);
    }

    public static String buildPatternWithArgs(final String root, final String identifier, final String cid, final Map<String, String> params) {
        final StringBuilder sb = new StringBuilder(buildPattern(root, identifier, cid));
        params.forEach((k, v) -> sb.append("&").append(k).append("=").append(v));
        return sb.toString();
    }

    public static String flattenSpecifiers(final List<String> specifiers) {
        return specifiers.size() == 1 ? specifiers.get(0) : String.join("/", specifiers);
    }

    private <T extends Enum<T> & IButtonType> T resolveEnumType() {
        final var enumClass = switch (this.getRoot()) {
            default -> throw new RuntimeException("Unknown root: " + this.getRoot());
            case "btn#cwu_session" -> SessionButtonType.class;
            case "mdl#cwu_session" -> SessionModalType.class;
            case "mnu#cwu_session" -> SessionMenuType.class;

            case "btn#cwu_profile" -> ProfileButtonType.class;

            case "btn#cwu_report" -> ReportButtonType.class;
            case "mdl#cwu_report" -> ReportModalType.class;
            case "mnu#cwu_report" -> ReportMenuType.class;
        };

        return Arrays.stream((T[]) enumClass.getEnumConstants())
                .filter(v -> v.getAction().equals(flattenSpecifiers(this.specifiers)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching enum found for root '" + this.getRoot() + "'"));
    }
}
