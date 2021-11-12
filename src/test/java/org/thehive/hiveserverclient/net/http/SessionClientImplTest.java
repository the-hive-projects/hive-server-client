package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Run this test while server is up.
@Slf4j
@ExtendWith(MockitoExtension.class)
class SessionClientImplTest {

    static String url = "http://localhost:8080/session";
    static long TIMEOUT_MS = 1_000L;

    SessionClient sessionClient;

    @BeforeEach
    void init() {
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.sessionClient = new SessionClientImpl(url, httpClient, objectMapper, threadPoolExecutor);
    }

    @DisplayName("Get existing session with successful authentication")
    @Test
    void getExistingSessionWithSuccessfulAuthentication() {
        final var id = "00000000000";
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Id: {}", id);
        log.info("Username: {}, Password: {}", username, password);
        var sessionRef=new AtomicReference<Session>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Session: {}", entity);
                sessionRef.set(entity);
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
            }
        };
        var spyCallback = spy(callback);
        sessionClient.get(id, spyCallback, authHeader);
        verify(spyCallback, after(TIMEOUT_MS)).onRequest(ArgumentMatchers.any(Session.class));
        verify(spyCallback, only()).onRequest(ArgumentMatchers.any(Session.class));
        verify(spyCallback, never()).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var session=sessionRef.get();
        assertNotNull(session);
    }

    @DisplayName("Get non-existing session with successful authentication")
    @Test
    void getNonExistingSessionWithSuccessfulAuthentication() {
        final var id = "00000000001";
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Id: {}", id);
        log.info("Username: {}, Password: {}", username, password);
        var errRef=new AtomicReference<Error>();
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Entity: {}", entity);
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
            }
        };
        var spyCallback = spy(callback);
        sessionClient.get(id, spyCallback, authHeader);
        verify(spyCallback, timeout(TIMEOUT_MS)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(1)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(0)).onRequest(ArgumentMatchers.any(Session.class));
        verify(spyCallback, times(0)).onFail(ArgumentMatchers.any(Throwable.class));
    }

    @DisplayName("Get with unsuccessful authentication")
    @Test
    void getWithUnsuccessfulAuthentication() {
        final var id = "00000000000";
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Id: {}", id);
        log.info("Username: {}, Password: {}", username, password);
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Entity: {}", entity);
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
            }
        };
        var spyCallback = spy(callback);
        sessionClient.get(id, spyCallback, authHeader);
        verify(spyCallback, timeout(TIMEOUT_MS)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(1)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(0)).onRequest(ArgumentMatchers.any(Session.class));
        verify(spyCallback, times(0)).onFail(ArgumentMatchers.any(Throwable.class));
    }

    @DisplayName("Save with successful authentication")
    @Test
    void saveWithSuccessfulAuthentication() {
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Name: {}", name);
        log.info("Username: {}, Password: {}", username, password);
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Entity: {}", entity);
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
            }
        };
        var spyCallback = spy(callback);
        sessionClient.save(session, spyCallback, authHeader);
        verify(spyCallback, timeout(TIMEOUT_MS)).onRequest(ArgumentMatchers.any(Session.class));
        verify(spyCallback, times(1)).onRequest(ArgumentMatchers.any(Session.class));
        verify(spyCallback, times(0)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(0)).onFail(ArgumentMatchers.any(Throwable.class));
    }

    @DisplayName("Save with unsuccessful authentication")
    @Test
    void saveWithUnsuccessfulAuthentication() {
        final var name = RandomStringUtils.randomAlphabetic(9, 17);
        var session = new Session(null, name, null, null);
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Name: {}", name);
        log.info("Username: {}, Password: {}", username, password);
        var callback = new RequestCallback<Session>() {
            @Override
            public void onRequest(Session entity) {
                log.info("Entity: {}", entity);
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
            }
        };
        var spyCallback = spy(callback);
        sessionClient.save(session, spyCallback, authHeader);
        verify(spyCallback, timeout(TIMEOUT_MS)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(1)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(0)).onRequest(ArgumentMatchers.any(Session.class));
        verify(spyCallback, times(0)).onFail(ArgumentMatchers.any(Throwable.class));
    }


}