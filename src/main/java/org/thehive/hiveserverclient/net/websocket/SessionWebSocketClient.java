package org.thehive.hiveserverclient.net.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;

public interface SessionWebSocketClient {

    SessionConnectionContext connect(String destination,
                                     WebSocketHttpHeaders handshakeHeaders,
                                     StompHeaders connectHeaders,
                                     SessionConnectionListener listener);

}
