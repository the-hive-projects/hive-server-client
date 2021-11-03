package org.thehive.hiveserverclient;

import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Session {

    public static final Session SESSION = new Session();
    private AtomicReference<String> tokenReference;
    private ConcurrentMap<String, Object> argumentMap;

    private Session() {
        this.tokenReference = new AtomicReference<>(null);
        this.argumentMap = new ConcurrentHashMap<>();
    }

    public void authenticate(@NonNull String token) {
        tokenReference.set(token);
    }

    public void unauthenticate() {
        tokenReference.set(null);
    }

    public void addArgument(String name, Object data) {
        argumentMap.put(name, data);
    }

    public boolean isAuthenticated() {
        return tokenReference.get() != null;
    }


    @SuppressWarnings("unchecked")
    public <T> T getArgument(String name, Class<T> type) {
        return (T) argumentMap.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String name) {
        return (T) argumentMap.get(name);
    }

    public boolean existsArgument(String name) {
        return argumentMap.containsKey(name);
    }

    public void clear() {
        argumentMap.clear();
    }

}
