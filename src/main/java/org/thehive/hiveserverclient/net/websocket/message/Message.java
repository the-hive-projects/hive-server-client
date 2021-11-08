package org.thehive.hiveserverclient.net.websocket.message;

import java.util.Map;

public interface Message<T> {

    MessageType getType();

    Map<String, Object> getHeaders();

    T getPayload();

}
