package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.models.CwuModel;

import java.util.*;

public class CwuDatabase extends JsonDatabase<String, CwuModel> {
    /* Singleton */
    private static CwuDatabase instance;
    public static CwuDatabase get() {
        if (instance == null) instance = new CwuDatabase();
        return instance;
    }

    private CwuDatabase() {
        super(new TypeReference<>() {
        }, "cwu_db.json");
        this.readFromCache().forEach(cwu -> map.put(cwu.getCid(), cwu));
    }

    @Override
    public void addOne(final CwuModel cwu) {
        this.map.put(cwu.getCid(), cwu);
    }

    /*
    Utils
    */
    public CwuModel getFromCid(final String cid) {
        return this.map.get(cid);
    }

    public CwuModel getFromId(final long id) {
        final var option = this.map.entrySet().stream().filter(cwu -> Objects.equals(cwu.getValue().getId(), id)).findFirst();
        return option.map(Map.Entry::getValue).orElse(null);
    }
}
