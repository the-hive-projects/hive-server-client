package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Image;
import org.thehive.hiveserverclient.net.http.ImageClientImpl;
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
@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    static final String URL = "http://localhost:8080/image";
    static final long TIMEOUT_MS_CALL = 3_000L;
    static final long TIMEOUT_MS_EXECUTE = 1_000L;

    ImageServiceImpl imageService;

    @BeforeEach
    void initialize() {
        var objectMapper = new ObjectMapper();
        var httpClient = HttpClients.createSystem();
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        var imageClient = new ImageClientImpl(URL, objectMapper, httpClient, threadPoolExecutor);
        this.imageService = new ImageServiceImpl(imageClient);
    }

    @BeforeEach
    void unauthenticate() {
        Authentication.INSTANCE.unauthenticate();
    }

    @Test
    @DisplayName("Take when authentication is correct")
    void takeWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(username, token);
        log.info("Username: {}, Password: {}", username, password);
        final var imageUsername = "username";
        log.info("ImageUsername: {}", imageUsername);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends Image>>();
        var consumer = new Consumer<AppResponse<? extends Image>>() {
            @Override
            public void accept(AppResponse<? extends Image> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        imageService.take(imageUsername, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResponseStatus.SUCCESS, result.status());
        verify(consumerSpy).accept(ArgumentMatchers.any());
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Take when authentication is incorrect")
    void takeWhenAuthenticationIsInCorrect() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(username, token);
        log.info("Username: {}, Password: {}", username, password);
        final var imageUsername = "username";
        log.info("ImageUsername: {}", imageUsername);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends Image>>();
        var consumer = new Consumer<AppResponse<? extends Image>>() {
            @Override
            public void accept(AppResponse<? extends Image> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        imageService.take(imageUsername, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResponseStatus.ERROR, result.status());
        verify(consumerSpy).accept(ArgumentMatchers.any());
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
    }

}