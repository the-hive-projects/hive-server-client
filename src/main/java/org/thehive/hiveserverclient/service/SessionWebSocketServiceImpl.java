package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.net.websocket.SessionConnectionContext;
import org.thehive.hiveserverclient.net.websocket.SessionConnectionListener;
import org.thehive.hiveserverclient.net.websocket.SessionWebSocketClientImpl;
import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.net.websocket.payload.Chat;
import org.thehive.hiveserverclient.net.websocket.payload.Information;
import org.thehive.hiveserverclient.net.websocket.payload.Payload;

@RequiredArgsConstructor
public class SessionWebSocketServiceImpl implements SessionWebSocketService {

    private final SessionWebSocketClientImpl sessionWebSocketClient;

    @Override
    public SessionConnectionContext connect(@NonNull String id, @NonNull SessionWebSocketListener listener) {
        var headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, Authentication.INSTANCE.getToken());
        return sessionWebSocketClient.connect(id, headers, null, new SessionConnectionListenerAdapter(listener));
    }

    private static class SessionConnectionListenerAdapter implements SessionConnectionListener {

        private final SessionWebSocketListener sessionListener;

        private SessionConnectionListenerAdapter(@NonNull SessionWebSocketListener sessionListener) {
            this.sessionListener = sessionListener;
        }

        @Override
        public void onConnect() {
            sessionListener.onConnect();
        }

        @Override
        public void onReceive(AppStompHeaders headers, Payload payload) {
            Class<? extends Payload> type = headers.getPayloadType().type;
            if (type == Chat.class)
                sessionListener.onChat((Chat) payload);
            else if (type == Information.class)
                sessionListener.onInformation((Information) payload);
            else
                throw new IllegalStateException("Payload type is not supported, type: " + type.getName());
        }

        @Override
        public void onSend(Payload payload) {
            sessionListener.onSend(payload);
        }

        @Override
        public void onException(Throwable t) {
            sessionListener.onException(t);
        }

        @Override
        public void onDisconnected() {
            sessionListener.onDisconnect();
        }

    }

}
