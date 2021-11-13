package org.thehive.hiveserverclient.net.websocket.subscription;

import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.payload.Payload;

public interface SubscriptionListener {

    void onSubscribe(StompSubscription subscription);

    void onSend(Payload payload);

    void onReceive(AppStompHeaders headers, Payload payload);

    void onUnsubscribe(StompSubscription subscription);

}
