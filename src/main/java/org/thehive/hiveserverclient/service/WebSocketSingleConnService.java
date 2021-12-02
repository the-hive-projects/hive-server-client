package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.net.websocket.WebSocketListener;

import java.util.Optional;

public interface WebSocketSingleConnService extends WebSocketService {

    boolean hasConnection();

    Optional<WebSocketSingleSubConnection> getConnection();

    @Override
    WebSocketSingleSubConnection connect(WebSocketListener listener);

}
