package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.net.websocket.WebSocketConnection;
import org.thehive.hiveserverclient.net.websocket.subscription.SessionSubscription;
import org.thehive.hiveserverclient.net.websocket.subscription.SubscriptionListener;

import java.util.Optional;

public interface WebSocketSingleSubConnection extends WebSocketConnection {

    boolean hasSessionSubscription();

    Optional<SessionSubscription> getSessionSubscription();

    @Override
    SessionSubscription subscribeToSession(String id, SubscriptionListener listener);

}
