package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import org.thehive.hiveserverclient.net.websocket.WebSocketConnection;
import org.thehive.hiveserverclient.net.websocket.subscription.SessionSubscription;
import org.thehive.hiveserverclient.net.websocket.subscription.SubscriptionListener;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class WebSocketSingleSubConnectionImpl extends WebSocketConnectionProxy implements WebSocketSingleSubConnection {

    private final AtomicReference<SessionSubscription> sessionSubscriptionReference;

    public WebSocketSingleSubConnectionImpl(@NonNull WebSocketConnection connection) {
        super(connection);
        this.sessionSubscriptionReference = new AtomicReference<>();
    }

    @Override
    public SessionSubscription subscribeToSession(String id, SubscriptionListener listener) {
        if (hasSessionSubscription())
            return sessionSubscriptionReference.get();
        var subscription = super.subscribeToSession(id, listener);
        sessionSubscriptionReference.set(subscription);
        return subscription;
    }

    @Override
    public boolean hasSessionSubscription() {
        return sessionSubscriptionReference.get() != null && sessionSubscriptionReference.get().isInSubscription();
    }

    @Override
    public Optional<SessionSubscription> getSessionSubscription() {
        return hasSessionSubscription() ? Optional.of(sessionSubscriptionReference.get()) : Optional.empty();
    }

}
