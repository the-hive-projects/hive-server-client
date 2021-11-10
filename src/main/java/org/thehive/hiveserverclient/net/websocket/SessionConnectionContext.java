package org.thehive.hiveserverclient.net.websocket;

import org.thehive.hiveserverclient.payload.Payload;

public interface SessionConnectionContext {

    String id();

    Status status();

    void send(String destination, Payload payload);

    void disconnect();

    enum Status {

        CONNECTING,
        CONNECTED,
        DISCONNECTED;

    }

}
