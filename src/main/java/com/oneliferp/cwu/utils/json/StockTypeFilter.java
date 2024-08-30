package com.oneliferp.cwu.utils.json;

import com.oneliferp.cwu.commands.modules.report.misc.StockType;

public class StockTypeFilter {
    @Override
    public boolean equals(Object obj) {
        return obj == StockType.UNKNOWN;
    }
}
