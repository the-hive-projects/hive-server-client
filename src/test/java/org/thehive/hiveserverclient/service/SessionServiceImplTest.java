package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.net.http.SessionClientImpl;
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
    void unauthenticate() {
        Authentication.INSTANCE.unauthenticate();
    }

    @DisplayName("Take existing session when authentication is correct")
    @Test
    void takeExistingSessionWhenAuthenticationIsCorrect() throws InterruptedException {
        final var id = "00000000000";
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionService.take(id, result -> {
            log.info("Result: {}", result);
            assertEquals(ResultStatus.TAKEN, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Take non-existing session when authentication is correct")
    @Test
    void takeNonExistingSessionWhenAuthenticationIsCorrect() throws InterruptedException {
        final var id = "00000000001";
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionService.take(id, result -> {
            log.info("Result: {}", result);
            assertEquals(ResultStatus.UNAVAILABLE, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Take session when authentication is incorrect")
    @Test
    void takeSessionWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var id = "00000000000";
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        sessionService.take(id, result -> {
            log.info("Result: {}", result);
            assertEquals(ResultStatus.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Create session when authenticated is correct")
    @Test
    void createSessionWhenAuthenticationIsCorrect() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphanumeric(9, 17);
        var session = new org.thehive.hiveserverclient.model.Session(null, name, null, null);
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("Session: {}", session);
        sessionService.create(session, result -> {
            log.info("Result: {}", result);
            assertEquals(ResultStatus.CREATED, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Create session when authentication is incorrect")
    @Test
    void createSessionWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var name = RandomStringUtils.randomAlphanumeric(9, 17);
        var session = new org.thehive.hiveserverclient.model.Session(null, name, null, null);
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("Session: {}", session);
        sessionService.create(session, result -> {
            log.info("Result: {}", result);
            assertEquals(ResultStatus.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

}