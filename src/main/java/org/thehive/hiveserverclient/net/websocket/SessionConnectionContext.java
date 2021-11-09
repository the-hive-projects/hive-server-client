package org.thehive.hiveserverclient.net.websocket;

import org.thehive.hiveserverclient.net.websocket.payload.Payload;

public interface SessionConnectionContext {

    Status status();

    void send(String destination, Payload payload);

    void disconnect();

    enum Status {

        CONNECTING,
        CONNECTED,
        DISCONNECTED;

    }

}
