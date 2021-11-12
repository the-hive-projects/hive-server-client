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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

// Run this test while server is up.
@Slf4j
@ExtendWith(MockitoExtension.class)
class ImageClientImplTest {

    static String URL = "http://localhost:8080/image";
    static long TIMEOUT_MS_CALL = 3_000L;
    static long TIMEOUT_MS_EXECUTE = 1_000L;

    ImageClient imageClient;

    @BeforeEach
    void init() {
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.imageClient = new ImageClientImpl(URL, httpClient, objectMapper, threadPoolExecutor);
    }

    @DisplayName("Get image with successful authentication")
    @Test
    void getImageWithSuccessfulAuthentication() throws InterruptedException {
        final var imageUsername = RandomStringUtils.randomAlphabetic(9, 17);
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("ImageUsername: {}", imageUsername);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var imgRef = new AtomicReference<Image>();
        var callback = new RequestCallback<Image>() {
            @Override
            public void onRequest(Image entity) {
                log.info("Image: {}", entity);
                imgRef.set(entity);
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
        var spyCallback = spy(callback);
        imageClient.get(imageUsername, spyCallback, authHeader);
        verify(spyCallback, timeout(TIMEOUT_MS_CALL)).onRequest(ArgumentMatchers.any(Image.class));
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(spyCallback, only()).onRequest(ArgumentMatchers.any(Image.class));
        verify(spyCallback, never()).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var image = imgRef.get();
        assertNotNull(image);
    }

    @DisplayName("Get image with unsuccessful authentication")
    @Test
    void getImageWithUnsuccessfulAuthentication() throws InterruptedException {
        final var imageUsername = RandomStringUtils.randomAlphabetic(9, 17);
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("ImageUsername: {}", imageUsername);
        log.info("Username: {}, Password: {}", username, password);
        var latch=new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<Image>() {
            @Override
            public void onRequest(Image entity) {
                log.error("Image: {}", entity);
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
        var spyCallback = spy(callback);
        imageClient.get(imageUsername, spyCallback, authHeader);
        verify(spyCallback, after(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any(Error.class));
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        verify(spyCallback, only()).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, never()).onRequest(ArgumentMatchers.any(Image.class));
        verify(spyCallback, never()).onFail(ArgumentMatchers.any(Throwable.class));
        var error = errRef.get();
        assertNotNull(error);
    }

}