package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.net.websocket.SessionConnectionContext;

public interface SessionWebSocketService {

    SessionConnectionContext connect(String id, SessionWebSocketListener listener);

}
