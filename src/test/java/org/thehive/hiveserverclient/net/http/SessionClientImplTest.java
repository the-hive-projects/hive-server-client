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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

// Run this test while server is up.
@Slf4j
@ExtendWith(MockitoExtension.class)
class SessionClientImplTest {

    static String URL = "http://localhost:8080/session";
    static long TIMEOUT_MS_CALL = 3_000L;
    static long TIMEOUT_MS_EXECUTE = 1_000L;

    SessionClient sessionClient;

    @BeforeEach
    void init() {
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.sessionClient = new SessionClientImpl(URL, httpClient, objectMapper, threadPoolExecutor);
    }

    @DisplayName("Get existing session with successful authentication")
    @Test
    void getExistingSessionWithSuccessfulAuthentication() throws InterruptedException {
        final var id = "00000000000";
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Id: {}", id);
        log.info("Username: {}, Password: {}", username, password);
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
            public void onError(Error e) {
                log.error("Error: {}", e);
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onRequest(ArgumentMatchers.any(Session.class));
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onRequest(ArgumentMatchers.any(Session.class));
        verify(callbackSpy, never()).onError(ArgumentMatchers.any(Error.class));
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var session = sessionRef.get();
        assertNotNull(session);
    }

    @DisplayName("Get non-existing session with successful authentication")
    @Test
    void getNonExistingSessionWithSuccessfulAuthentication() throws InterruptedException {
        final var id = "00000000001";
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Id: {}", id);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.error("Session: {}", entity);
            }

            @Override
            public void onError(Error e) {
                log.info("Error: {}", e);
                errRef.set(e);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any(Error.class));
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onError(ArgumentMatchers.any(Error.class));
        verify(callbackSpy, never()).onRequest(ArgumentMatchers.any(Session.class));
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var error = errRef.get();
        assertNotNull(error);
    }

    @DisplayName("Get with unsuccessful authentication")
    @Test
    void getWithUnsuccessfulAuthentication() throws InterruptedException {
        final var id = "00000000000";
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Id: {}", id);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.error("Session: {}", entity);
            }

            @Override
            public void onError(Error e) {
                log.info("Error: {}", e);
                errRef.set(e);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any(Error.class));
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onError(ArgumentMatchers.any(Error.class));
        verify(callbackSpy, never()).onRequest(ArgumentMatchers.any(Session.class));
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var error = errRef.get();
        assertNotNull(error);
    }

    @DisplayName("Save with successful authentication")
    @Test
    void saveWithSuccessfulAuthentication() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Name: {}", name);
        log.info("Username: {}, Password: {}", username, password);
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
            public void onError(Error e) {
                log.error("Error: {}", e);
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.save(session, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onRequest(ArgumentMatchers.any(Session.class));
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onRequest(ArgumentMatchers.any(Session.class));
        verify(callbackSpy, never()).onError(ArgumentMatchers.any(Error.class));
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var responseSession = sessionRef.get();
        assertNotNull(responseSession);
    }

    @DisplayName("Save with unsuccessful authentication")
    @Test
    void saveWithUnsuccessfulAuthentication() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Name: {}", name);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.error("Session: {}", entity);
            }

            @Override
            public void onError(Error e) {
                log.info("Error: {}", e);
                errRef.set(e);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.error(t.getMessage());
            }
        };
        var callbackSpy = spy(callback);
        sessionClient.save(session, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any(Error.class));
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(callbackSpy, only()).onError(ArgumentMatchers.any(Error.class));
        verify(callbackSpy, never()).onRequest(ArgumentMatchers.any(Session.class));
        verify(callbackSpy, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var err = errRef.get();
        assertNotNull(err);
    }

}