package org.thehive.hiveserverclient.net.websocket;

import org.thehive.hiveserverclient.net.websocket.subscription.SessionSubscription;
import org.thehive.hiveserverclient.net.websocket.subscription.SubscriptionListener;

public interface WebSocketConnection {

    ConnectionStatus status();

    SessionSubscription subscribeToSession(String id, SubscriptionListener listener);

    void disconnect();

}
