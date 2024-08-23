package com.oneliferp.cwu.cache;

import java.util.HashMap;

public abstract class RuntimeCache<A, B> {
    private final HashMap<A, B> objects;

    protected RuntimeCache() {
        objects = new HashMap<>();
    }

    public void add(final A key, final B object) {
        System.out.println("Added object: '" + object + "' with key: '" + key + "'");
        this.objects.put(key, object);
    }

    public B get(final A key) {
        return this.objects.get(key);
    }

    public void remove(final A key) {
        this.objects.remove(key);
    }

    public boolean exist(final A key) {
        return this.objects.containsKey(key);
    }
}
