package org.thehive.hiveserverclient.net.websocket;

import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.payload.Payload;

public interface SessionConnectionListener {

    void onConnect();

    void onReceive(AppStompHeaders headers, Payload payload);

    void onSend(Payload payload);

    void onException(Throwable t);

    void onDisconnect();

}
