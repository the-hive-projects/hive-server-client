package org.thehive.hiveserverclient.net.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.payload.Payload;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
public class SessionWebSocketClientImpl implements SessionWebSocketClient {

    private final String connectionUrl;
    private final String subscriptionEndpoint;
    private final WebSocketStompClient webSocketStompClient;
    private final ThreadPoolExecutor executor;

    @Override
    public SessionConnectionContext connect(String id, WebSocketHttpHeaders handshakeHeaders,
                                            StompHeaders connectHeaders, SessionConnectionListener listener) {
        var onExecutorListener = new OnExecutorSessionConnectionListener(listener, executor);
        return this.connect(id, handshakeHeaders, connectHeaders, onExecutorListener);
    }

    private SessionConnectionContext connect(String id, WebSocketHttpHeaders handshakeHeaders,
                                             StompHeaders connectHeaders, OnExecutorSessionConnectionListener listener) {

        var connectionContext = new SessionConnectionContextImpl(id, listener);
        webSocketStompClient.connect(connectionUrl, handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                connectionContext.connect(session);
                executor.execute(listener::onConnect);
                var endpoint = subscriptionEndpoint + "/" + id;
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

    private static class OnExecutorSessionConnectionListener implements SessionConnectionListener {

        private final SessionConnectionListener listener;
        private final Executor executor;

        public OnExecutorSessionConnectionListener(@NonNull SessionConnectionListener listener, @NonNull Executor executor) {
            this.listener = listener;
            this.executor = executor;
        }

        @Override
        public void onConnect() {
            executor.execute(listener::onConnect);
        }

        @Override
        public void onReceive(AppStompHeaders headers, Payload payload) {
            executor.execute(() -> listener.onReceive(headers, payload));
        }

        @Override
        public void onSend(Payload payload) {
            executor.execute(() -> listener.onSend(payload));
        }

        @Override
        public void onException(Throwable t) {
            executor.execute(() -> listener.onException(t));
        }

        @Override
        public void onDisconnect() {
            executor.execute(listener::onDisconnect);
        }

    }

    private static class SessionConnectionContextImpl implements SessionConnectionContext {

        private final String id;
        private final SessionConnectionListener listener;
        private volatile Status status;
        private volatile StompSession session;

        private SessionConnectionContextImpl(@NonNull String id, @NonNull SessionConnectionListener listener) {
            this.id = id;
            this.listener = listener;
            this.status = Status.CONNECTING;
            this.session = null;
        }

        private void connect(@NonNull StompSession session) {
            if (status != Status.CONNECTING)
                throw new IllegalStateException("Session connection status: " + status.name());
            this.status = Status.CONNECTED;
            this.session = session;
        }

        @Override
        public String id() {
            return id;
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
        }

    }

}
