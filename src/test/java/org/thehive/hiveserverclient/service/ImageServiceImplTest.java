package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.net.http.ImageClientImpl;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Run this test while server is up.
@Slf4j
class ImageServiceImplTest {

    ImageServiceImpl imageService;

    @BeforeEach
    void initialize() {
        var url = "http://localhost:8080/image";
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        var imageClient = new ImageClientImpl(url, httpClient, objectMapper, threadPoolExecutor);
        this.imageService = new ImageServiceImpl(imageClient);
    }

    @BeforeEach
    void unauthenticate() {
        Authentication.INSTANCE.unauthenticate();
    }

    @DisplayName("Take when authentication is correct")
    @Test
    void takeWhenAuthenticationIsCorrect() throws InterruptedException {
        final var imageUsername = "username";
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("ImageUsername: {}", imageUsername);
        imageService.take(imageUsername, result -> {
            log.info("Result: {}", result);
            assertEquals(ResultStatus.TAKEN, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Take when authentication is incorrect")
    @Test
    void takeWhenAuthenticationIsInCorrect() throws InterruptedException {
        final var imageUsername = "username";
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("ImageUsername: {}", imageUsername);
        imageService.take(imageUsername, result -> {
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