package com.oneliferp.cwu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.JsonDatabase;
import com.oneliferp.cwu.utils.SimpleDateTime;

import java.io.FileReader;
import java.io.FileWriter;

public class Config {
    private static final String FILE_PATH = "persistence/config.json";

    @JsonProperty("start_period")
    private SimpleDateTime startPeriodDate;

    private Config() {
    }

    public SimpleDateTime getStartPeriodDate() {
        return this.startPeriodDate;
    }

    public void setStartPeriodDate(final SimpleDateTime date) {
        this.startPeriodDate = date;
    }

    /* Persistence */
    public void save() {
        try (final FileWriter fw = new FileWriter(FILE_PATH)) {
            JsonDatabase.MAPPER.writeValue(fw, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Config load() {
        try (FileReader fr = new FileReader(FILE_PATH)) {
            return JsonDatabase.MAPPER.readValue(fr, Config.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public void ensureReady() {
        if (this.startPeriodDate != null) return;

        this.startPeriodDate = SimpleDateTime.now();
        this.save();
    }
}
