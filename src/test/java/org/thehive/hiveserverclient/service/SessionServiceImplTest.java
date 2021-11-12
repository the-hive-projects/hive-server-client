package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.net.http.SessionClientImpl;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Run this test while server is up.
@Slf4j
class SessionServiceImplTest {

    static final String URL = "http://localhost:8080/session";
    static final long TIMEOUT_MS_CALL = 3_000L;
    static final long TIMEOUT_MS_EXECUTE = 1_000L;

    SessionServiceImpl sessionService;

    @BeforeEach
    void initialize() {
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        var sessionClient = new SessionClientImpl(URL, objectMapper, httpClient, threadPoolExecutor);
        this.sessionService = new SessionServiceImpl(sessionClient);
    }

    @BeforeEach
    void unauthenticate() {
        Authentication.INSTANCE.unauthenticate();
    }

    @Test
    @DisplayName("Take existing session when authentication is correct")
    void takeExistingSessionWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var id = "00000000000";
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<Result<? extends Session>>();
        var consumer = new Consumer<Result<? extends Session>>() {
            @Override
            public void accept(Result<? extends Session> result) {
                log.info("Result: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        sessionService.take(id, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResultStatus.SUCCESS, result.status());
    }

    @Test
    @DisplayName("Take non-existing session when authentication is correct")
    void takeNonExistingSessionWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var id = "11111111111";
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<Result<? extends Session>>();
        var consumer = new Consumer<Result<? extends Session>>() {
            @Override
            public void accept(Result<? extends Session> result) {
                log.info("Result: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        sessionService.take(id, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResultStatus.ERROR_UNAVAILABLE, result.status());
    }

    @Test
    @DisplayName("Take session when authentication is incorrect")
    void takeSessionWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var id = "00000000000";
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<Result<? extends Session>>();
        var consumer = new Consumer<Result<? extends Session>>() {
            @Override
            public void accept(Result<? extends Session> result) {
                log.info("Result: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        sessionService.take(id, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResultStatus.ERROR, result.status());
    }

    @Test
    @DisplayName("Create session when authenticated is correct")
    void createSessionWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var name = RandomStringUtils.randomAlphanumeric(9, 17);
        var session = new Session(null, name, null, null);
        log.info("Session: {}", session);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<Result<? extends Session>>();
        var consumer = new Consumer<Result<? extends Session>>() {
            @Override
            public void accept(Result<? extends Session> result) {
                log.info("Result: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        sessionService.create(session, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResultStatus.SUCCESS, result.status());
    }

    @Test
    @DisplayName("Create session when authentication is incorrect")
    void createSessionWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var name = RandomStringUtils.randomAlphanumeric(9, 17);
        var session = new Session(null, name, null, null);
        log.info("Session: {}", session);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<Result<? extends Session>>();
        var consumer = new Consumer<Result<? extends Session>>() {
            @Override
            public void accept(Result<? extends Session> result) {
                log.info("Result: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        sessionService.create(session, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResultStatus.ERROR, result.status());
    }

}