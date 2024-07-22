package com.oneliferp.cwu.Database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class JsonDatabase<T> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Type type;
    private final File file;
    private List<T> objects;

    protected JsonDatabase(final Class<T> type, final String fileName) {
        this.type = TypeToken.getParameterized(List.class, type).getType();

        final Path directoryPath = Paths.get("Databases");
        if (!Files.exists(directoryPath)) {
            try { Files.createDirectories(directoryPath);}
            catch (IOException e) { throw new RuntimeException("Failed to create Databases directory."); }
        }

        this.file = new File(directoryPath.toFile(), fileName);
        this.objects = new ArrayList<>();
    }

    protected void saveOne(final T object) {
        this.objects.add(object);
    }

    protected void saveMany(final List<T> objectList) {
        this.objects.addAll(objectList);
    }

    protected void removeOne(final T object) {
        this.objects.remove(object);
    }

    protected final List<T> getAll() {
        return this.objects;
    }

    protected final void readFromCache() {
        try (final FileReader reader = new FileReader(this.file)){
            this.objects = GSON.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void writeToCache() {
        try (final FileWriter writer = new FileWriter(file)) {
            GSON.toJson(objects, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
