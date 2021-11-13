package org.thehive.hiveserverclient.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.net.websocket.UrlEndpointResolverImpl;
import org.thehive.hiveserverclient.net.websocket.WebSocketClientImpl;
import org.thehive.hiveserverclient.net.websocket.WebSocketConnection;
import org.thehive.hiveserverclient.net.websocket.WebSocketListener;
import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.net.websocket.subscription.StompSubscription;
import org.thehive.hiveserverclient.payload.Chat;
import org.thehive.hiveserverclient.payload.Payload;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

// Run this test while server is up.
@Slf4j
@ExtendWith(MockitoExtension.class)
class WebSocketServiceImplTest {

    static final long TIMEOUT_MS_CALL = 3_000L;
    static final long TIMEOUT_MS_EXECUTE = 1_000L;
    static String URL = "ws://localhost:8080/stomp";
    static String SUBSCRIPTION_ENDPOINT = "/topic/session/{id}";
    static String DESTINATION_PREFIX = "/websocket";
    static String PAYLOAD_CHAT_ENDPOINT = "/session/chat/{id}";

    WebSocketServiceImpl webSocketService;

    @BeforeEach
    void init() {
        var urlEndpointResolver = new UrlEndpointResolverImpl(SUBSCRIPTION_ENDPOINT, DESTINATION_PREFIX);
        urlEndpointResolver.addDestinationUrlEndpoint(Chat.class, PAYLOAD_CHAT_ENDPOINT);
        var wsClient = new StandardWebSocketClient();
        var wsStompClient = new WebSocketStompClient(wsClient);
        wsStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        var executorService = Executors.newCachedThreadPool();
        var webSocketClient = new WebSocketClientImpl(URL, urlEndpointResolver, wsStompClient, executorService);
        this.webSocketService = new WebSocketServiceImpl(webSocketClient);
    }

    @BeforeEach
    void unauthenticate() {
        Authentication.INSTANCE.unauthenticate();
    }


    @Test
    @DisplayName("Connect with successful authentication")
    void connectWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var connRef = new AtomicReference<WebSocketConnection>();
        var listener = new WebSocketListener() {
            @Override
            public void onConnect(WebSocketConnection connection) {
                log.info("onConnect");
                connRef.set(connection);
                latch.countDown();
            }

            @Override
            public void onSubscribe(StompSubscription subscription) {
                log.info("onSubscribe");
            }

            @Override
            public void onUnsubscribe(StompSubscription subscription) {
                log.info("onUnsubscribe");
            }

            @Override
            public void onReceive(AppStompHeaders headers, Payload payload) {
                log.info("onReceive");
            }

            @Override
            public void onSend(Payload payload) {
                log.info("onSend");
            }

            @Override
            public void onException(Throwable t) {
                log.info("onException");
            }

            @Override
            public void onDisconnect(WebSocketConnection connection) {
                log.info("onDisconnect");
            }
        };
        var listenerSpy = Mockito.spy(listener);
        webSocketService.connect(listenerSpy);
        verify(listenerSpy, timeout(TIMEOUT_MS_CALL)).onConnect(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var conn = connRef.get();
        assertNotNull(conn);
        verify(listenerSpy).onConnect(ArgumentMatchers.any());
        verify(listenerSpy, only()).onConnect(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Connect with unsuccessful authentication")
    void connectWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var expRef = new AtomicReference<Throwable>();
        var listener = new WebSocketListener() {
            @Override
            public void onConnect(WebSocketConnection connection) {
                log.info("onConnect");
            }

            @Override
            public void onSubscribe(StompSubscription subscription) {
                log.info("onSubscribe");
            }

            @Override
            public void onUnsubscribe(StompSubscription subscription) {
                log.info("onUnsubscribe");
            }

            @Override
            public void onReceive(AppStompHeaders headers, Payload payload) {
                log.info("onReceive");
            }

            @Override
            public void onSend(Payload payload) {
                log.info("onSend");
            }

            @Override
            public void onException(Throwable t) {
                log.info("onException");
                expRef.set(t);
                latch.countDown();
            }

            @Override
            public void onDisconnect(WebSocketConnection connection) {
                log.info("onDisconnect");
            }
        };
        var listenerSpy = Mockito.spy(listener);
        webSocketService.connect(listenerSpy);
        verify(listenerSpy, timeout(TIMEOUT_MS_CALL)).onException(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var exp = expRef.get();
        assertNotNull(exp);
        verify(listenerSpy).onException(ArgumentMatchers.any());
        verify(listenerSpy, only()).onException(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Connect and disconnect with successful authentication")
    void connectAndDisconnectWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        var connLatch = new CountDownLatch(1);
        var disconnLatch = new CountDownLatch(1);
        var connRef = new AtomicReference<WebSocketConnection>();
        var listener = new WebSocketListener() {
            @Override
            public void onConnect(WebSocketConnection connection) {
                log.info("onConnect");
                connRef.set(connection);
                connLatch.countDown();
            }

            @Override
            public void onSubscribe(StompSubscription subscription) {
                log.info("onSubscribe");
            }

            @Override
            public void onUnsubscribe(StompSubscription subscription) {
                log.info("onUnsubscribe");
            }

            @Override
            public void onReceive(AppStompHeaders headers, Payload payload) {
                log.info("onReceive");
            }

            @Override
            public void onSend(Payload payload) {
                log.info("onSend");
            }

            @Override
            public void onException(Throwable t) {
                log.info("onException");
            }

            @Override
            public void onDisconnect(WebSocketConnection connection) {
                log.info("onDisconnect");
                connRef.set(connection);
                disconnLatch.countDown();
            }
        };
        var listenerSpy = Mockito.spy(listener);
        var connection = webSocketService.connect(listenerSpy);
        verify(listenerSpy, timeout(TIMEOUT_MS_CALL)).onConnect(ArgumentMatchers.any());
        var connCompleted = connLatch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!connCompleted)
            fail(new IllegalStateException("Callback execution timed out"));
        var connConnection = connRef.get();
        assertNotNull(connConnection);
        connection.disconnect();
        verify(listenerSpy, timeout(TIMEOUT_MS_CALL)).onDisconnect(ArgumentMatchers.any());
        var disconnCompleted = disconnLatch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!disconnCompleted)
            fail(new IllegalStateException("Callback execution timed out"));
        var disconnConnection = connRef.get();
        assertNotNull(disconnConnection);
        verify(listenerSpy).onConnect(ArgumentMatchers.any());
        verify(listenerSpy).onDisconnect(ArgumentMatchers.any());
        verify(listenerSpy, never()).onException(ArgumentMatchers.any());
        verify(listenerSpy, never()).onReceive(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(listenerSpy, never()).onSend(ArgumentMatchers.any());
        verify(listenerSpy, never()).onSubscribe(ArgumentMatchers.any());
        verify(listenerSpy, never()).onUnsubscribe(ArgumentMatchers.any());
    }


}