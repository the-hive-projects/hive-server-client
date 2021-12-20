package org.thehive.hiveserverclient;

import lombok.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class Authentication {

    public static final Authentication INSTANCE = new Authentication();

    private final AtomicReference<String> usernameReference;
    private final AtomicReference<String> tokenReference;

    private Authentication() {
        this.usernameReference = new AtomicReference<>(null);
        this.tokenReference = new AtomicReference<>(null);
    }

    public String getUsername() {
        return usernameReference.get();
    }

    public String getToken() {
        return tokenReference.get();
    }

    public void authenticate(@NonNull String username, @NonNull String token) {
        usernameReference.set(username);
        tokenReference.set(token);
    }

    public void unauthenticate() {
        tokenReference.set(null);
        usernameReference.set(null);
    }

    public boolean isAuthenticated() {
        return tokenReference.get() != null;
    }

}
