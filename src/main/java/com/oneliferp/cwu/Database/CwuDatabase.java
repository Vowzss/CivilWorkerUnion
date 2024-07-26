package com.oneliferp.cwu.Database;

import com.oneliferp.cwu.Models.CwuModel;

import java.util.HashMap;
import java.util.List;

public class CwuDatabase extends JsonDatabase<CwuModel> {
    private static CwuDatabase instance;
    public static CwuDatabase get() {
        if (instance == null) instance = new CwuDatabase();
        return instance;
    }

    private final HashMap<String, CwuModel> cwuMap;

    private CwuDatabase() {
        super(CwuModel.class, "cwu_data.json");
        this.cwuMap = new HashMap<>();

        this.getAll().forEach(cwu -> {
            this.cwuMap.put(cwu.getCid(), cwu);
        });
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
    public CwuModel get(final String cid) {
        return this.cwuMap.get(cid);
    }

    public void removeOne(final String cid) {
        final CwuModel cwu = this.cwuMap.remove(cid);
        this.removeOne(cwu);
    }

    public boolean contains(final String cid) {
        return this.cwuMap.containsKey(cid);
    }
}
