package org.thehive.hiveserverclient.net.websocket.subscription;

import org.thehive.hiveserverclient.payload.Payload;

public interface StompSubscription {

    boolean inSubscription();

    void send(Payload payload);

    void unsubscribe();

}
