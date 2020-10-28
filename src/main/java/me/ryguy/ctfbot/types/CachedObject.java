package me.ryguy.ctfbot.types;

import java.util.function.Supplier;

public class CachedObject<T> {

    long duration;
    long lastCache;
    T object;
    Supplier<T> supplier;

    public CachedObject(long duration, Supplier<T> getter) {
        this.object = getter.get();
        this.supplier = getter;
        this.duration = duration;
        this.lastCache = System.currentTimeMillis();
    }
    public T get() {
        if(System.currentTimeMillis() > (lastCache + duration)) {
            return getNew();
        } else {
            return getCached();
        }
    }
    public T getCached() {
        return object;
    }
    public T getNew() {
        this.lastCache = System.currentTimeMillis();
        return supplier.get();
    }
}
