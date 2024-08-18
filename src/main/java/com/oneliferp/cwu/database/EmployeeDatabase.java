package com.oneliferp.cwu.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.EmployeeModel;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDatabase extends JsonDatabase<String, EmployeeModel> {
    /* Singleton */
    private static EmployeeDatabase instance;
    public static EmployeeDatabase get() {
        if (instance == null) instance = new EmployeeDatabase();
        return instance;
    }

    private EmployeeDatabase() {
        super(new TypeReference<>() {
        }, "employee_db.json");
        this.readFromCache().forEach(cwu -> map.put(cwu.getCid(), cwu));
    }

    @Override
    public void addOne(final EmployeeModel cwu) {
        this.map.put(cwu.getCid(), cwu);
    }

    /*
    Utils
    */
    public EmployeeModel getFromCid(final String cid) {
        return this.map.get(cid);
    }

    public EmployeeModel getFromId(final long id) {
        final var option = this.map.entrySet().stream().filter(cwu -> Objects.equals(cwu.getValue().getId(), id)).findFirst();
        return option.map(Map.Entry::getValue).orElse(null);
    }

    public Map<CwuBranch, List<EmployeeModel>> getAsGroupAndOrder() {
        return this.getAll().stream()
                .collect(Collectors.groupingBy(
                        EmployeeModel::getBranch,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingInt(employee -> employee.getRank().getOrder()))
                                        .toList()
                        )
                ));
    }
}
