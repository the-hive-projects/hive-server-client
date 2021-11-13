package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import org.apache.http.HttpHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.net.websocket.WebSocketClient;
import org.thehive.hiveserverclient.net.websocket.WebSocketConnection;
import org.thehive.hiveserverclient.net.websocket.WebSocketListener;

public class WebSocketServiceImpl implements WebSocketService {

    private final WebSocketClient webSocketClient;

    public WebSocketServiceImpl(@NonNull WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Override
    public WebSocketConnection connect( WebSocketListener listener) {
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        var headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, Authentication.INSTANCE.getToken());
        return webSocketClient.connect(headers, null, listener);
    }

}
