package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.fail;

// Run this test while server is up.
@Slf4j
class SessionClientImplTest {

    SessionClient sessionClient;

    @BeforeEach
    void initialize() {
        var url = "http://localhost:8080/session";
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.sessionClient = new SessionClientImpl(url, httpClient, objectMapper, threadPoolExecutor);
    }

    @DisplayName("Get existing session with successful authentication")
    @Test
    void getExistingSessionWithSuccessfulAuthentication() throws InterruptedException {
        final var id = "00000000000";
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionClient.get(id, new RequestCallback<>() {

            @Override
            public void onRequest(Session entity) {
                log.info("Entity: {}", entity);
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
                fail(e.getMessage());
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }

    @DisplayName("Get non-existing session with successful authentication")
    @Test
    void getNonExistingSessionWithSuccessfulAuthentication() throws InterruptedException {
        final var id = "00000000001";
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionClient.get(id, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                log.warn("Entity: {}", entity);
                fail("'get' request must get error when session does not exist");
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                log.info("Error: {}", e);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }

    @DisplayName("Get with unsuccessful authentication")
    @Test
    void getWithUnsuccessfulAuthentication() throws InterruptedException {
        final var id = "00000000000";
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionClient.get(id, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                log.warn("Entity: {}", entity);
                fail("'get' request must get error when authentication is unsuccessful");
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                log.info("Error: {}", e);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }

    @DisplayName("Save with successful authentication")
    @Test
    void saveWithSuccessfulAuthentication() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("Session: {}",session);
        sessionClient.save(session, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Entity: {}", entity);
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
                fail(e.getMessage());
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }

    @DisplayName("Save with unsuccessful authentication")
    @Test
    void saveWithUnsuccessfulAuthentication() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("Session: {}",session);
        sessionClient.save(session, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                log.warn("Entity: {}", entity);
                fail("'save' request must get error when authentication is unsuccessful");
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                log.info("Error: {}", e);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }


}