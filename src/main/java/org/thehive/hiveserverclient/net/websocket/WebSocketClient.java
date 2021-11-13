package org.thehive.hiveserverclient.net.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;

public interface WebSocketClient {

    WebSocketConnection connect(WebSocketHttpHeaders handshakeHeaders, StompHeaders connectHeaders, WebSocketListener listener);

}
