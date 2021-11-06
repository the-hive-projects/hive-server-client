package org.thehive.hiveserverclient;

import lombok.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class Authentication {

    public static Authentication INSTANCE = new Authentication();

    private AtomicReference<String> tokenReference;

    private Authentication() {
        this.tokenReference = new AtomicReference<>(null);
    }

    public String getToken() {
        return tokenReference.get();
    }

    public void authenticate(@NonNull String token) {
        tokenReference.set(token);
    }

    public void unauthenticate() {
        tokenReference.set(null);
    }

    public boolean isAuthenticated() {
        return tokenReference.get() != null;
    }

}
