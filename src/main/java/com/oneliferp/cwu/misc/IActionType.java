package com.oneliferp.cwu.misc;

import com.oneliferp.cwu.commands.utils.CommandContext;

import java.util.Map;
import java.util.Objects;

public interface IActionType {
    String getRoot();

    String getAction();

    default String build(final String cid) {
        return CommandContext.buildPattern(this.getRoot(), this.getAction(), cid);
    }

    default String build() {
        return CommandContext.buildGlobalPattern(this.getRoot(), this.getAction());
    }

    default String build(final String cid, final Map<String, String> params) {
        return CommandContext.buildPatternWithArgs(this.getRoot(), this.getAction(), cid, params);
    }
}
