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
import java.util.ArrayList;
import java.util.List;

public abstract class JsonDatabase<T> {
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

    private final TypeReference<List<T>> typeRef;

    private final File file;
    protected final List<T> objects;

    protected JsonDatabase(final TypeReference<List<T>> typeRef, final String fileName) {
        this.typeRef = typeRef;

        final Path directoryPath = Paths.get("Databases");
        if (!Files.exists(directoryPath)) {
            try { Files.createDirectories(directoryPath);}
            catch (IOException e) { throw new RuntimeException("Failed to create Databases directory."); }
        }

        this.file = new File(directoryPath.toFile(), fileName);
        this.objects = new ArrayList<>();

        this.readFromCache();
    }

    /*
    Update methods
    */
    public void addOne(final T object) {
        this.objects.add(object);
    }

    public void addMany(final List<T> objectList) {
        this.objects.addAll(objectList);
    }

    public void removeOne(final T object) {
        this.objects.remove(object);
    }

    public final List<T> getAll() {
        return this.objects;
    }

    /*
    Persistence methods
    */
    private void readFromCache() {
        if (!file.exists()) return;

        try (final FileReader reader = new FileReader(this.file)) {
            this.objects.addAll(MAPPER.readValue(reader, typeRef));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void writeToCache() {
        synchronized (this.objects) {
            try (final FileWriter writer = new FileWriter(file)) {
                MAPPER.writeValue(writer, this.objects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
