package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.net.websocket.WebSocketConnection;
import org.thehive.hiveserverclient.net.websocket.WebSocketListener;

public interface WebSocketService {

    WebSocketConnection connect(WebSocketListener listener);

}
