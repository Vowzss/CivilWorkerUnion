package com.oneliferp.cwu.misc;

import com.oneliferp.cwu.commands.utils.CommandContext;

import java.util.Map;

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
        if (params == null) return this.build(cid);
        return CommandContext.buildPatternWithArgs(this.getRoot(), this.getAction(), cid, params);
    }
}
