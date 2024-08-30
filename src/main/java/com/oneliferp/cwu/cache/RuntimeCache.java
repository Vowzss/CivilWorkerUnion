package com.oneliferp.cwu.cache;

import java.util.HashMap;

public abstract class RuntimeCache<A, B> {
    private final HashMap<A, B> objects;

    protected RuntimeCache() {
        this.objects = new HashMap<>();
    }

    public void add(final A key, final B object) {
        System.out.println("Added object: '" + object + "' with key: '" + key + "'");
        this.objects.put(key, object);
    }

    public B find(final A key) {
        return this.objects.get(key);
    }

    public void delete(final A key) {
        final var object = this.objects.remove(key);
        System.out.println("Deleted object: '" + object + "' with key: '" + key + "'");
    }

    public boolean exist(final A key) {
        return this.objects.containsKey(key);
    }
}
