package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.net.http.SessionClientImpl;
import org.thehive.hiveserverclient.service.status.CreateSessionStatus;
import org.thehive.hiveserverclient.service.status.TakeSessionStatus;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Run this test while server is up.
@Slf4j
class SessionServiceImplTest {

    SessionServiceImpl sessionService;

    @BeforeEach
    void initialize() {
        var url = "http://localhost:8080/session";
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        var sessionClient = new SessionClientImpl(url, httpClient, objectMapper, threadPoolExecutor);
        this.sessionService = new SessionServiceImpl(sessionClient);
    }

    @BeforeEach
    void resetSession() {
        org.thehive.hiveserverclient.Session.SESSION.unauthenticate();
        org.thehive.hiveserverclient.Session.SESSION.clear();
    }

    @DisplayName("Take existing session after session is authenticated")
    @Test
    void takeExistingSessionAfterSessionIsAuthenticated() throws InterruptedException {
        final var id = "00000000000";
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        org.thehive.hiveserverclient.Session.SESSION.authenticate(token);
        org.thehive.hiveserverclient.Session.SESSION.addArgument("header", HeaderUtils.httpBasicAuthenticationHeader(token));
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionService.take(id, result -> {
            log.info("Result: {}", result);
            assertEquals(TakeSessionStatus.TAKEN, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Take non-existing session after session is authenticated")
    @Test
    void takeNonExistingSessionAfterSessionIsAuthenticated() throws InterruptedException {
        final var id = "00000000001";
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        org.thehive.hiveserverclient.Session.SESSION.authenticate(token);
        org.thehive.hiveserverclient.Session.SESSION.addArgument("header", HeaderUtils.httpBasicAuthenticationHeader(token));
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionService.take(id, result -> {
            log.info("Result: {}", result);
            assertEquals(TakeSessionStatus.UNAVAILABLE, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Take session when session is not authenticated")
    @Test
    void takeSessionWhenSessionIsNotAuthenticated() throws InterruptedException {
        final var id = "00000000000";
        var latch = new CountDownLatch(1);
        sessionService.take(id, result -> {
            log.info("Result: {}", result);
            assertEquals(TakeSessionStatus.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Create session after session is authenticated")
    @Test
    void createSessionAfterSessionIsAuthenticated() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphanumeric(9, 17);
        var session = new org.thehive.hiveserverclient.model.Session(null, name, null, null);
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        org.thehive.hiveserverclient.Session.SESSION.authenticate(token);
        org.thehive.hiveserverclient.Session.SESSION.addArgument("header", HeaderUtils.httpBasicAuthenticationHeader(token));
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("Session: {}", session);
        sessionService.create(session, result -> {
            log.info("Result: {}", result);
            assertEquals(CreateSessionStatus.CREATED, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Create session when session is not authenticated")
    @Test
    void createSessionWhenSessionIsNotAuthenticated() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphanumeric(9, 17);
        var session = new org.thehive.hiveserverclient.model.Session(null, name, null, null);
        var latch = new CountDownLatch(1);
        log.info("Session: {}", session);
        sessionService.create(session, result -> {
            log.info("Result: {}", result);
            assertEquals(CreateSessionStatus.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

}