package org.thehive.hiveserverclient.net.websocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.thehive.hiveserverclient.net.websocket.header.AppStompHeaders;
import org.thehive.hiveserverclient.payload.Chat;
import org.thehive.hiveserverclient.payload.Payload;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Run this test while server is up.
@Slf4j
class SessionWebSocketClientImplTest {

    SessionWebSocketClientImpl sessionWebSocketClient;

    @BeforeEach
    void init() {
        var connectionUrl = "ws://localhost:8080/stomp";
        var subscriptionEndpoint = "/topic/{id}";
        var sessionUrlEndpointResolver = new SessionUrlEndpointResolverImpl(subscriptionEndpoint);
        sessionUrlEndpointResolver.addDestinationUrlEndpoint(Chat.class, "/websocket/chat/{id}");
        var webSocketClient = new StandardWebSocketClient();
        var webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        this.sessionWebSocketClient = new SessionWebSocketClientImpl(connectionUrl, sessionUrlEndpointResolver, webSocketStompClient, executor);
    }

    @DisplayName("Connect with successful authentication")
    @Test
    void connectWithSuccessfulAuthentication() throws InterruptedException {
        final var id = RandomStringUtils.randomNumeric(11);
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        var headers = new WebSocketHttpHeaders();
        headers.add(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, token);
        var latch = new CountDownLatch(1);
        sessionWebSocketClient.connect(id, headers, null, new SessionConnectionListener() {
            @Override
            public void onConnect() {
                log.info("onConnect");
                log.info("Connected successfully");
                latch.countDown();
            }

            @Override
            public void onReceive(AppStompHeaders headers, Payload payload) {
                log.info("onReceive");
                fail("callback onReceive is executed");
                latch.countDown();
            }

            @Override
            public void onSend(Payload payload) {
                log.info("onSend");
                fail("callback onSend is executed");
                latch.countDown();
            }

            @Override
            public void onException(Throwable t) {
                log.info("onException");
                fail("callback onException is executed", t);
                latch.countDown();
            }

            @Override
            public void onDisconnect() {
                log.info("onDisconnect");
                fail("callback onDisconnect is executed");
                latch.countDown();
            }
        });
        latch.await();
    }

    @DisplayName("Connect with unsuccessful authentication")
    @Test
    void connectWithUnsuccessfulAuthentication() throws InterruptedException {
        final var id = RandomStringUtils.randomNumeric(11);
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        var headers = new WebSocketHttpHeaders();
        headers.add(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, token);
        var latch = new CountDownLatch(1);
        sessionWebSocketClient.connect(id, headers, null, new SessionConnectionListener() {
            @Override
            public void onConnect() {
                log.info("onConnect");
                fail("callback onConnect is executed");
                latch.countDown();
            }

            @Override
            public void onReceive(AppStompHeaders headers, Payload payload) {
                log.info("onReceive");
                fail("callback onReceive is executed");
                latch.countDown();
            }

            @Override
            public void onSend(Payload payload) {
                log.info("onSend");
                fail("callback onSend is executed");
                latch.countDown();
            }

            @Override
            public void onException(Throwable t) {
                log.info("onException");
                log.info("Cannot be connected");
                log.info("Exception: {}, message: {}", t.getClass().getName(), t.getMessage());
                latch.countDown();
            }

            @Override
            public void onDisconnect() {
                log.info("onDisconnect");
                fail("callback onDisconnect is executed");
                latch.countDown();
            }
        });
        latch.await();
    }

    @DisplayName("Connect and send chat payload")
    @Test
    void connectAndSendChatPayload() throws InterruptedException {
        final var id = RandomStringUtils.randomNumeric(11);
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        var headers = new WebSocketHttpHeaders();
        headers.add(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, token);
        var chat = new Chat(null, "Hello", 0L);
        var latch = new CountDownLatch(1);
        var connectionLatch = new CountDownLatch(1);
        var connectionCtx = sessionWebSocketClient.connect(id, headers, null, new SessionConnectionListener() {
            @Override
            public void onConnect() {
                log.info("onConnect");
                log.info("Connected successfully");
                connectionLatch.countDown();
            }

            @Override
            public void onReceive(AppStompHeaders headers, Payload payload) {
                log.info("onReceive");
                log.info("Message was received successfully");
                log.info("Message: {}", payload);
                assertEquals(Chat.class, payload.getClass());
                assertEquals(Chat.class, headers.getPayloadType().type);
                latch.countDown();
            }

            @Override
            public void onSend(Payload payload) {
                log.info("onSend");
                log.info("Message is being sent");
                log.info("Message: {}", payload);
            }

            @Override
            public void onException(Throwable t) {
                log.info("onException");
                log.info("Cannot be connected");
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onDisconnect() {
                log.info("onDisconnect");
                fail("callback onDisconnect is executed");
                latch.countDown();
            }
        });
        connectionLatch.await();
        connectionCtx.send(chat);
        latch.await();
    }


}