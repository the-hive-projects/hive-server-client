package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.thehive.hiveserverclient.model.Image;
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
class ImageClientImplTest {

    static final String URL = "http://localhost:8080/image";
    static final long TIMEOUT_MS_CALL = 3_000L;
    static final long TIMEOUT_MS_EXECUTE = 1_000L;

    ImageClient imageClient;

    @BeforeEach
    void init() {
        var objectMapper = new ObjectMapper();
        var httpClient = HttpClients.createSystem();
        var executorService = Executors.newSingleThreadExecutor();
        this.imageClient = new ImageClientImpl(URL, objectMapper, httpClient, executorService);
    }

    @Test
    @DisplayName("Get image with successful authentication")
    void getImageWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var imageUsername = RandomStringUtils.randomAlphabetic(9, 17);
        log.info("Image username: {}", imageUsername);
        var latch = new CountDownLatch(1);
        var imgRef = new AtomicReference<Image>();
        var callback = new RequestCallback<Image>() {
            @Override
            public void onResponse(Image responseBody) {
                log.info("Image: {}", responseBody);
                imgRef.set(responseBody);
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
        imageClient.get(imageUsername, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onResponse(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var image = imgRef.get();
        assertNotNull(image);
        verify(callbackSpy).onResponse(ArgumentMatchers.any());
        verify(callbackSpy, only()).onResponse(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Get image with unsuccessful authentication")
    void getImageWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var imageUsername = RandomStringUtils.randomAlphabetic(9, 17);
        log.info("Image username: {}", imageUsername);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<Image>() {
            @Override
            public void onResponse(Image responseBody) {
                log.error("Image: {}", responseBody);
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
        imageClient.get(imageUsername, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var error = errRef.get();
        assertNotNull(error);
        verify(callbackSpy).onError(ArgumentMatchers.any());
        verify(callbackSpy, only()).onError(ArgumentMatchers.any());
    }

}