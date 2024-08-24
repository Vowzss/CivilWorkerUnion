package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;

import java.util.*;
import java.util.stream.Collectors;

public class ProfileDatabase extends JsonDatabase<String, ProfileModel> {
    /* Singleton */
    private static ProfileDatabase instance;

    public static ProfileDatabase get() {
        if (instance == null) instance = new ProfileDatabase();
        return instance;
    }

    private ProfileDatabase() {
        super(new TypeReference<>() {
        }, "profile_db.json");
    }

    @Override
    public void addOne(final ProfileModel cwu) {
        System.out.printf("CID: '%s', IDENTITY: '%s'%n", cwu.getCid(), cwu.getIdentity());
        this.map.put(cwu.getCid(), cwu);
    }

    /* Utils */
    public ProfileModel getFromCid(final String cid) {
        return this.map.get(cid);
    }

    public ProfileModel getFromId(final long id) {
        final var option = this.map.entrySet().stream().filter(cwu -> Objects.equals(cwu.getValue().getId(), id)).findFirst();
        return option.map(Map.Entry::getValue).orElse(null);
    }

    public ProfileModel getSupervisor() {
        return this.map.values().stream()
                .filter(e -> e.getRank() == CwuRank.SUPERVISOR)
                .findFirst()
                .orElse(null);
    }

    public ProfileModel getAssistant() {
        return this.map.values().stream()
                .filter(e -> e.getRank() == CwuRank.ASSISTANT)
                .findFirst()
                .orElse(null);
    }

    public Map<CwuBranch, List<ProfileModel>> getAsGroupAndOrder() {
        return this.getAll().stream()
                .collect(Collectors.groupingBy(
                        ProfileModel::getBranch,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingInt(employee -> employee.getRank().ordinal()))
                                        .toList()
                        )
                ));
    }
}
