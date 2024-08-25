package com.oneliferp.cwu.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.utils.SimpleDuration;
import com.oneliferp.cwu.utils.json.SimpleDateDeserializer;
import com.oneliferp.cwu.utils.json.SimpleDurationDeserializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class JsonDatabase<A, B> {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    static {
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        final SimpleModule module = new SimpleModule();
        module.addDeserializer(SimpleDate.class, new SimpleDateDeserializer());
        module.addDeserializer(SimpleDuration.class, new SimpleDurationDeserializer());

        MAPPER.registerModule(module);
    }

    private final TypeReference<List<B>> typeRef;
    private final File file;

    protected final HashMap<A, B> map;

    protected JsonDatabase(final TypeReference<List<B>> typeRef, final String fileName) {
        this.typeRef = typeRef;

        final Path directoryPath = Paths.get("Databases");
        if (!Files.exists(directoryPath)) {
            try { Files.createDirectories(directoryPath);}
            catch (IOException e) { throw new RuntimeException("Failed to create Databases directory."); }
        }

        this.file = new File(directoryPath.toFile(), fileName);
        this.map = new HashMap<>();

        this.load();
    }

    /* Methods */
    public abstract void addOne(final B value);

    public void addMany(final Collection<B> values) {
        values.forEach(this::addOne);
    }

    public void removeOne(final A cid) {
        this.map.remove(cid);
    }

    public boolean exist(final A cid) {
        return this.map.containsKey(cid);
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
    protected void load() {
        this.addMany(this.readFromCache());
        System.out.printf("Loaded: '%d' values from file: '%s'.\n\n", this.map.values().size(), this.file);
    }

    public void save() {
        this.writeToCache(this.getAll());
    }

    public void clear() {
        this.clearCache();
    }

    /* Disk methods */
    private Collection<B> readFromCache() {
        if (!file.exists()) return new ArrayList<>();

        try (final FileReader fr = new FileReader(this.file)) {
            return MAPPER.readValue(fr, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private void clearCache() {
        try (final FileWriter fw = new FileWriter(this.file)) {
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToCache(final Collection<B> objects) {
        try (final FileWriter fw = new FileWriter(this.file)) {
            MAPPER.writeValue(fw, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
