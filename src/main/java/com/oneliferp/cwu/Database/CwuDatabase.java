package com.oneliferp.cwu.Database;

import com.oneliferp.cwu.Models.CwuModel;

import java.util.HashMap;
import java.util.List;

public class CwuDatabase extends JsonDatabase<CwuModel> {
    private static CwuDatabase instance;
    public static CwuDatabase getInstance() {
        if (instance == null) instance = new CwuDatabase();
        return instance;
    }

    private final HashMap<Long, CwuModel> cwuMap;

    private CwuDatabase() {
        super(CwuModel.class, "cwu_data.json");
        this.cwuMap = new HashMap<>();

        this.readFromCache();
        this.getAll().forEach(cwu -> this.cwuMap.put(cwu.getUserID(), cwu));
    }

    @Override
    public void saveOne(final CwuModel cwu) {
        super.saveOne(cwu);
        this.cwuMap.put(cwu.getUserID(), cwu);
    }

    @Override
    public void saveMany(final List<CwuModel> cwuList) {
        super.saveMany(cwuList);
        cwuList.forEach(this::saveOne);
    }

    /*
    Getters
    */
    public CwuModel getByUserId(final Long userID) {
        return this.cwuMap.get(userID);
    }

    public CwuModel getByCid(final String cid) {
        return this.cwuMap.entrySet().stream().filter(entry -> entry.getValue().getCid().equals(cid)).findFirst().get().getValue();
    }

    public void remove(final Long userID) {
        final CwuModel cwu = this.cwuMap.remove(userID);
        this.removeOne(cwu);
    }

    public boolean contains(final Long userID) {
        return this.cwuMap.containsKey(userID);
    }
}
