package org.thehive.hiveserverclient.net.websocket.subscription;

import org.thehive.hiveserverclient.payload.Payload;

public interface StompSubscription {

    String getDestination();

    boolean isInSubscription();

    void send(Payload payload);

    void unsubscribe();

}
