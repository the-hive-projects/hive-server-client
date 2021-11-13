package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import org.thehive.hiveserverclient.net.websocket.ConnectionStatus;
import org.thehive.hiveserverclient.net.websocket.WebSocketConnection;
import org.thehive.hiveserverclient.net.websocket.subscription.SessionSubscription;
import org.thehive.hiveserverclient.net.websocket.subscription.SubscriptionListener;

public class WebSocketConnectionProxy implements WebSocketConnection {

    private final WebSocketConnection connection;

    public WebSocketConnectionProxy(@NonNull WebSocketConnection connection) {
        this.connection = connection;
    }

    @Override
    public ConnectionStatus status() {
        return connection.status();
    }

    @Override
    public SessionSubscription subscribeToSession(String id, SubscriptionListener listener) {
        return connection.subscribeToSession(id, listener);
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }

}
