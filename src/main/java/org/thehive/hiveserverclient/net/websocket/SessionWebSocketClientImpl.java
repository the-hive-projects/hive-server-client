package org.thehive.hiveserverclient.net.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.net.websocket.payload.Payload;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class SessionWebSocketClientImpl implements SessionWebSocketClient {

    private final String connectionUrl;
    private final String subscriptionEndpoint;
    private final WebSocketStompClient webSocketStompClient;

    @Override
    public SessionConnectionContext connect(String id, WebSocketHttpHeaders handshakeHeaders,
                                            StompHeaders connectHeaders, SessionConnectionListener listener) {
        var connectionContext = new SessionConnectionContextImpl(listener);
        webSocketStompClient.connect(connectionUrl, handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                connectionContext.connect(session);
                listener.onConnect();
                var endpoint=subscriptionEndpoint+"/"+id;
                session.subscribe(endpoint, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        var appStompHeaders = new AppStompHeaders(headers);
                        var payloadType = appStompHeaders.getPayloadType();
                        return payloadType.type;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        listener.onReceive(new AppStompHeaders(headers), (Payload) payload);
                    }
                });
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                listener.onException(exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                listener.onException(exception);
            }

        });
        return connectionContext;
    }

    private static class SessionConnectionContextImpl implements SessionConnectionContext {

        private final SessionConnectionListener listener;
        private volatile Status status;
        private volatile StompSession session;

        private SessionConnectionContextImpl(@NonNull SessionConnectionListener listener) {
            this.listener = listener;
            this.status = Status.CONNECTED;
            this.session = null;
        }

        private void connect(@NonNull StompSession session) {
            if (status != Status.CONNECTING)
                throw new IllegalStateException("Session connection status: " + status.name());
            this.status = Status.CONNECTED;
            this.session = session;
        }

        @Override
        public Status status() {
            return status;
        }

        @Override
        public void send(@NonNull String destination, @NonNull Payload payload) {
            if (status != Status.CONNECTED)
                throw new IllegalStateException("Session connection status: " + status.name());
            session.send(destination, payload);
            listener.onSend(payload);
        }

        @Override
        public void disconnect() {
            if (status != Status.CONNECTED)
                throw new IllegalStateException("Session connection status: " + status.name());
            this.status = Status.DISCONNECTED;
            session.disconnect();
            listener.onDisconnected();
        }

    }

}
