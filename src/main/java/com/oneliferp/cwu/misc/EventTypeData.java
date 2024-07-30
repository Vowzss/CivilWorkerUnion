package com.oneliferp.cwu.misc;

import com.oneliferp.cwu.modules.profile.misc.ProfileButtonType;
import com.oneliferp.cwu.modules.session.misc.SessionButtonType;
import com.oneliferp.cwu.modules.session.misc.SessionModalType;

import java.util.Arrays;

public class EventTypeData {
    public final String root;
    public final String action;
    public final String specifier;
    public final String id;

    public final Enum<?> enumType;

    public EventTypeData(final String input) {
        final String[] parts = input.split("\\.", 2);
        this.root = parts[0];

        final String[] subParts = parts[1].split(":", 2);
        this.id = subParts[1];

        final String[] actionParts = subParts[0].split("/", 2);
        this.action = actionParts[0];
        this.specifier = actionParts.length < 2 ? null : actionParts[1];

        this.enumType = switch (this.root) {
            default -> throw new RuntimeException("Unknown root: " + this.root);
            case "btn$cwu_session" -> Arrays.stream(SessionButtonType.values())
                    .filter(v -> v.action.equals(this.action + (this.specifier == null ? "" : "/" + this.specifier)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No matching button found at: " + this.action));

            case "mdl$cwu_session" -> Arrays.stream(SessionModalType.values())
                    .filter(v -> v.action.equals(this.action + (this.specifier == null ? "" : "/" + this.specifier)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No matching modal found at: " + this.action));

            case "btn$cwu_profile" -> Arrays.stream(ProfileButtonType.values())
                    .filter(v -> v.action.equals(this.action + (this.specifier == null ? "" : "/" + this.specifier)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No matching button found at: " + this.action));
        };

        System.out.println(input);
    }
}
