package com.oneliferp.cwu.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.oneliferp.cwu.utils.SimpleDateTime;
import com.oneliferp.cwu.utils.SimpleDuration;
import com.oneliferp.cwu.utils.json.SimpleDateDeserializer;
import com.oneliferp.cwu.utils.json.SimpleDurationDeserializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class JsonDatabase<A, B> {
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    static {
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        final SimpleModule module = new SimpleModule();
        module.addDeserializer(SimpleDateTime.class, new SimpleDateDeserializer());
        module.addDeserializer(SimpleDuration.class, new SimpleDurationDeserializer());

        MAPPER.registerModule(module);
    }

    protected final Path directory;
    protected final HashMap<A, B> map;
    private final TypeReference<List<B>> typeRef;

    protected JsonDatabase(final TypeReference<List<B>> typeRef, final Path directory) {
        this.typeRef = typeRef;
        this.directory = directory;

        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectories(this.directory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create database directory.");
            }
        }

        this.map = new HashMap<>();
    }

    /* Methods */
    public abstract void addOne(final B value);

    public void addMany(final Collection<B> values) {
        values.forEach(this::addOne);
    }

    public void removeOne(final A key) {
        this.map.remove(key);
    }

    public boolean exist(final A key) {
        return this.map.containsKey(key);
    }

    public List<B> getAll() {
        return this.map.values().stream().toList();
    }

    public B get(final A key) {
        return this.map.get(key);
    }

    public int getCount() {
        return this.map.size();
    }

    /* Persistence methods */
    protected void load(final File file) {
        this.addMany(this.readFromCache(file));
        System.out.printf("Loaded: '%d' values from file: '%s'.\n\n", this.map.values().size(), file);
    }

    public abstract void save();

    public abstract void clear();

    /* Disk methods */
    protected Collection<B> readFromCache(final File file) {
        if (!file.exists()) return new ArrayList<>();

        try (final FileReader fr = new FileReader(file)) {
            return MAPPER.readValue(fr, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    protected void clearCache(final File file) {
        try (final FileWriter fw = new FileWriter(file)) {
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeToCache(final Collection<B> objects, final File file) {
        try (final FileWriter fw = new FileWriter(file)) {
            MAPPER.writeValue(fw, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
