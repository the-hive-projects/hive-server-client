package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;
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
class SessionClientImplTest {

    static final String URL = "http://localhost:8080/session";
    static final long TIMEOUT_MS_CALL = 3_000L;
    static final long TIMEOUT_MS_EXECUTE = 1_000L;

    SessionClient sessionClient;

    @BeforeEach
    void init() {
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        var executorService = Executors.newSingleThreadExecutor();
        this.sessionClient = new SessionClientImpl(URL, objectMapper, httpClient, executorService);
    }

    @Test
    @DisplayName("Get existing session with successful authentication")
    void getExistingSessionWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var id = "00000000000";
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var sessionRef = new AtomicReference<Session>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Session: {}", entity);
                sessionRef.set(entity);
                latch.countDown();
            }

            @Override
            public void onError(Error error) {
                log.error("Error: {}", error);
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onRequest(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onRequest(ArgumentMatchers.any());
        verify(callbackSpy, never()).onError(ArgumentMatchers.any());
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any());
        var session = sessionRef.get();
        assertNotNull(session);
    }

    @Test
    @DisplayName("Get non-existing session with successful authentication")
    void getNonExistingSessionWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var id = "11111111111";
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.error("Session: {}", entity);
            }

            @Override
            public void onError(Error error) {
                log.info("Error: {}", error);
                errRef.set(error);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onError(ArgumentMatchers.any());
        verify(callbackSpy, never()).onRequest(ArgumentMatchers.any());
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any());
        var error = errRef.get();
        assertNotNull(error);
    }

    @Test
    @DisplayName("Get with unsuccessful authentication")
    void getWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var id = "00000000000";
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.error("Session: {}", entity);
            }

            @Override
            public void onError(Error error) {
                log.info("Error: {}", error);
                errRef.set(error);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onError(ArgumentMatchers.any());
        verify(callbackSpy, never()).onRequest(ArgumentMatchers.any());
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any());
        var error = errRef.get();
        assertNotNull(error);
    }

    @Test
    @DisplayName("Save with successful authentication")
    void saveWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        log.info("Session: {}", session);
        var latch = new CountDownLatch(1);
        var sessionRef = new AtomicReference<Session>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Session: {}", entity);
                sessionRef.set(entity);
                latch.countDown();
            }

            @Override
            public void onError(Error error) {
                log.error("Error: {}", error);
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.save(session, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onRequest(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onRequest(ArgumentMatchers.any());
        verify(callbackSpy, never()).onError(ArgumentMatchers.any());
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any());
        var responseSession = sessionRef.get();
        assertNotNull(responseSession);
    }

    @Test
    @DisplayName("Save with unsuccessful authentication")
    void saveWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        log.info("Name: {}", name);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.error("Session: {}", entity);
            }

            @Override
            public void onError(Error error) {
                log.info("Error: {}", error);
                errRef.set(error);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.save(session, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onError(ArgumentMatchers.any());
        verify(callbackSpy, never()).onRequest(ArgumentMatchers.any());
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any());
        var err = errRef.get();
        assertNotNull(err);
    }

}