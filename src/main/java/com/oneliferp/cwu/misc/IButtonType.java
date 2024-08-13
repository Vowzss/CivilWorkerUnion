package com.oneliferp.cwu.misc;

import com.oneliferp.cwu.commands.CommandContext;

import java.util.Map;

public interface IButtonType {
    String getRoot();

    String getAction();

    default String build(final String cid) {
        return CommandContext.buildPattern(this.getRoot(), this.getAction(), cid);
    }

    default String build(final String cid, final Map<String, String> params) {
        return CommandContext.buildPatternWithArgs(this.getRoot(), this.getAction(), cid, params);
    }
}
