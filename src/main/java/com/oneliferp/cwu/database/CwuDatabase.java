package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.models.CwuModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CwuDatabase extends JsonDatabase<CwuModel> {
    private static CwuDatabase instance;

    public static CwuDatabase getInstance() {
        if (instance == null) instance = new CwuDatabase();
        return instance;
    }

    private final HashMap<String, CwuModel> cwuMap;

    private CwuDatabase() {
        super(new TypeReference<>() {}, "cwu_data.json");

        this.cwuMap = new HashMap<>();
        this.getAll() .forEach(cwu -> this.cwuMap.put(cwu.getCid(), cwu));
    }

    @Override
    public void addOne(final CwuModel cwu) {
        super.addOne(cwu);
        this.cwuMap.put(cwu.getCid(), cwu);
    }

    @Override
    public void addMany(final List<CwuModel> cwuList) {
        super.addMany(cwuList);
        cwuList.forEach(this::addOne);
    }

    /*
    Getters
    */
    public CwuModel getFromCid(final String cid) {
        return this.cwuMap.get(cid);
    }

    public CwuModel getFromId(final long id) {
        final var option = this.cwuMap.entrySet().stream().filter(cwu -> Objects.equals(cwu.getValue().getId(), id)).findFirst();
        return option.map(Map.Entry::getValue).orElse(null);
    }

    public void removeOne(final String cid) {
        final CwuModel cwu = this.cwuMap.remove(cid);
        this.removeOne(cwu);
    }

    public boolean contains(final String cid) {
        return this.cwuMap.containsKey(cid);
    }
}
