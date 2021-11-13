package org.thehive.hiveserverclient.net.websocket;

import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.net.websocket.subscription.StompSubscription;
import org.thehive.hiveserverclient.payload.Payload;

public interface WebSocketListener {

    void onConnect(WebSocketConnection connection);

    void onSubscribe(StompSubscription subscription);

    void onUnsubscribe(StompSubscription subscription);

    void onReceive(AppStompHeaders headers, Payload payload);

    void onSend(Payload payload);

    void onException(Throwable t);

    void onDisconnect(WebSocketConnection connection);

}
