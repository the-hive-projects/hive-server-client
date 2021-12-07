package org.thehive.hiveserverclient.net.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.net.websocket.subscription.SessionSubscription;
import org.thehive.hiveserverclient.net.websocket.subscription.StompSubscription;
import org.thehive.hiveserverclient.net.websocket.subscription.SubscriptionListener;
import org.thehive.hiveserverclient.payload.Payload;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class WebSocketClientImpl implements WebSocketClient {

    private final String connectionUrl;
    private final UrlEndpointResolver urlEndpointResolver;
    private final WebSocketStompClient webSocketStompClient;
    private final ExecutorService executorService;

    @Override
    public WebSocketConnection connect(WebSocketHttpHeaders handshakeHeaders, StompHeaders connectHeaders, WebSocketListener listener) {
        var onExecutorListener = new WebSocketOnExecutorListener(listener, executorService);
        return this.connect(handshakeHeaders, connectHeaders, onExecutorListener);
    }

    private WebSocketConnection connect(WebSocketHttpHeaders handshakeHeaders, StompHeaders connectHeaders, WebSocketOnExecutorListener listener) {
        var connection = new WebSocketConnectionImpl(listener, urlEndpointResolver);
        webSocketStompClient.connect(connectionUrl, handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                connection.connect(session);
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
        return connection;

    }


    private static class WebSocketOnExecutorListener implements WebSocketListener {

        private final WebSocketListener listener;
        private final Executor executor;

        public WebSocketOnExecutorListener(@NonNull WebSocketListener listener, @NonNull Executor executor) {
            this.listener = listener;
            this.executor = executor;
        }

        @Override
        public void onConnect(WebSocketConnection connection) {
            executor.execute(() -> listener.onConnect(connection));
        }

        @Override
        public void onSubscribe(StompSubscription subscription) {
            executor.execute(() -> listener.onSubscribe(subscription));
        }

        @Override
        public void onUnsubscribe(StompSubscription subscription) {
            executor.execute(() -> listener.onUnsubscribe(subscription));
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
        public void onDisconnect(WebSocketConnection connection) {
            executor.execute(() -> listener.onDisconnect(connection));
        }

    }


    private static class WebSocketConnectionImpl implements WebSocketConnection {

        private final WebSocketListener webSocketListener;
        private final UrlEndpointResolver urlEndpointResolver;
        private final AtomicReference<StompSession> sessionReference;
        private volatile ConnectionStatus status;

        public WebSocketConnectionImpl(@NonNull WebSocketListener webSocketListener, @NonNull UrlEndpointResolver urlEndpointResolver) {
            this.webSocketListener = webSocketListener;
            this.urlEndpointResolver = urlEndpointResolver;
            this.sessionReference = new AtomicReference<>();
            this.status = ConnectionStatus.CONNECTING;
        }

        private void connect(StompSession session) {
            if (status != ConnectionStatus.CONNECTING)
                throw new IllegalStateException();
            sessionReference.set(session);
            status = ConnectionStatus.CONNECTED;
            webSocketListener.onConnect(this);
        }

        @Override
        public ConnectionStatus status() {
            return status;
        }

        @Override
        public SessionSubscription subscribeToSession(String id, SubscriptionListener listener) {
            if (status != ConnectionStatus.CONNECTED)
                throw new IllegalStateException();
            var destination = urlEndpointResolver.resolveSubscriptionUrlEndpoint(id);
            var subscription = sessionReference.get().subscribe(destination, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    var appStompHeaders = new AppStompHeaders(headers);
                    var payloadType = appStompHeaders.getPayloadType();
                    return payloadType.type;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    var appStompHeaders = new AppStompHeaders(headers);
                    var appPayload = (Payload) payload;
                    webSocketListener.onReceive(appStompHeaders, appPayload);
                    listener.onReceive(appStompHeaders, appPayload);
                }
            });
            return new SessionSubscriptionImpl(id, destination, listener, subscription);
        }

        @Override
        public void disconnect() {
            if (status != ConnectionStatus.CONNECTED)
                throw new IllegalStateException();
            sessionReference.get().disconnect();
            status = ConnectionStatus.DISCONNECTED;
            webSocketListener.onDisconnect(this);
        }

        private class SessionSubscriptionImpl implements SessionSubscription {

            private final String id;
            private final String destination;
            private final SubscriptionListener subscriptionListener;
            private final StompSession.Subscription subscription;
            private volatile boolean inSubscription;

            public SessionSubscriptionImpl(String id, String destination, SubscriptionListener subscriptionListener, StompSession.Subscription subscription) {
                this.id = id;
                this.destination = destination;
                this.subscriptionListener = subscriptionListener;
                this.subscription = subscription;
                this.inSubscription = true;
                webSocketListener.onSubscribe(this);
                subscriptionListener.onSubscribe(this);
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public void send(Payload payload) {
                if (!isInSubscription())
                    throw new IllegalStateException();
                var endpoint = urlEndpointResolver.resolveDestinationUrlEndpoint(payload.getClass(), id);
                sessionReference.get().send(endpoint, payload);
                webSocketListener.onSend(payload);
                subscriptionListener.onSend(payload);
            }

            @Override
            public void unsubscribe() {
                if (!isInSubscription())
                    throw new IllegalStateException();
                subscription.unsubscribe();
                inSubscription = false;
                webSocketListener.onUnsubscribe(this);
                subscriptionListener.onUnsubscribe(this);
            }

            @Override
            public String getDestination() {
                return destination;
            }

            @Override
            public boolean isInSubscription() {
                return inSubscription;
            }
        }


    }


}
