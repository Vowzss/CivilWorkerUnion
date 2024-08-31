package com.oneliferp.cwu.commands.utils;

import com.oneliferp.cwu.commands.modules.manage.misc.actions.ManageModalType;
import com.oneliferp.cwu.commands.modules.manage.misc.actions.WorkforceButtonType;
import com.oneliferp.cwu.commands.modules.panel.misc.actions.PanelButtonType;
import com.oneliferp.cwu.commands.modules.panel.misc.actions.PanelModalType;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileButtonType;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileMenuType;
import com.oneliferp.cwu.commands.modules.profile.misc.actions.ProfileModalType;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportButtonType;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportMenuType;
import com.oneliferp.cwu.commands.modules.report.misc.actions.ReportModalType;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionButtonType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionMenuType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionModalType;
import com.oneliferp.cwu.misc.IActionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContext {
    public final String type;
    public final String identifier;
    public final List<String> specifiers;
    public final HashMap<String, String> params;

    public CommandContext(final String input) {
        final String[] parts = input.split("#", 2);
        this.type = parts[0];

        final String[] idAndRest = parts[1].split("/", 2);
        this.identifier = idAndRest[0];

        final String[] specifiersAndParams = idAndRest[1].split("\\?", 2);
        this.specifiers = Arrays.asList(specifiersAndParams[0].split("/"));

        this.params = new HashMap<>();
        if (specifiersAndParams.length > 1) {
            Arrays.stream(specifiersAndParams[1].split("&")).forEach(param -> {
                final var pair = param.split("=", 2);
                this.params.put(pair[0], pair[1]);
            });
        }

        System.out.printf("""
                Type: %s
                Identifier: %s
                Specifiers: %s
                Params: %s
                %n""", this.type, this.identifier, this.specifiers, this.params);
    }

    /*
    Utils
    */
    public static String buildPattern(final String root, final String identifier, final String cid) {
        return String.format("%s/%s?cid=%s", root, identifier, cid);
    }

    public static String buildGlobalPattern(final String root, final String identifier) {
        return String.format("%s/%s", root, identifier);
    }

    public static String buildPatternWithArgs(final String root, final String identifier, final String cid, final Map<String, String> params) {
        final StringBuilder sb = new StringBuilder(buildPattern(root, identifier, cid));
        params.forEach((k, v) -> sb.append("&").append(k).append("=").append(v));
        return sb.toString();
    }

    public static String flattenSpecifiers(final List<String> specifiers) {
        return specifiers.size() == 1 ? specifiers.get(0) : String.join("/", specifiers);
    }

    public String getCid() {
        return this.getAsString("cid");
    }

    public SessionType getSession() {
        return SessionType.valueOf(this.getAsString("session"));
    }

    public String getRoot() {
        return String.format("%s#%s", this.type, this.identifier);
    }

    public String getAsString(final String key) {
        return this.params.get(key);
    }

    public int getAsInt(final String key) {
        return Integer.parseInt(this.params.get(key));
    }

    public String getCommand() {
        return this.identifier.split("_")[1];
    }

    public <T extends Enum<T> & IActionType> T getEnumType() {
        final var enumClass = switch (this.getRoot()) {
            default -> throw new RuntimeException("Unknown root: " + this.getRoot());
            case "btn#cwu_session" -> SessionButtonType.class;
            case "mdl#cwu_session" -> SessionModalType.class;
            case "mnu#cwu_session" -> SessionMenuType.class;

            case "btn#cwu_profile" -> ProfileButtonType.class;
            case "mdl#cwu_profile" -> ProfileModalType.class;
            case "mnu#cwu_profile" -> ProfileMenuType.class;

            case "btn#cwu_report" -> ReportButtonType.class;
            case "mdl#cwu_report" -> ReportModalType.class;
            case "mnu#cwu_report" -> ReportMenuType.class;

            case "btn#cwu_panel" -> PanelButtonType.class;
            case "mdl#cwu_panel" -> PanelModalType.class;

            case "btn#cwu_manage" -> switch (this.specifiers.get(0)) {
                case "report" -> ReportButtonType.class;
                case "workforce" -> WorkforceButtonType.class;
                case "session" -> SessionButtonType.class;
                default -> throw new IllegalStateException("Unexpected value: " + this.specifiers.get(0));
            };

            case "mdl#cwu_manage" -> ManageModalType.class;
        };

        return Arrays.stream((T[]) enumClass.getEnumConstants())
                .filter(v -> v.getAction().equals(flattenSpecifiers(this.specifiers)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching enum found for root '" + this.getRoot() + "'"));
    }
}
