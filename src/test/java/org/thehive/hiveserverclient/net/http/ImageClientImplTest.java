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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Mockito.*;

// Run this test while server is up.
@Slf4j
@ExtendWith(MockitoExtension.class)
class ImageClientImplTest {

    static int TIMEOUT_MS = 5_000;

    ImageClient imageClient;

    @BeforeEach
    void initialize() {
        var url = "http://localhost:8080/image";
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.imageClient = new ImageClientImpl(url, httpClient, objectMapper, threadPoolExecutor);
    }

    @DisplayName("Get image with successful authentication")
    @Test
    void getImageWithSuccessfulAuthentication() {
        final var imageUsername = RandomStringUtils.randomAlphabetic(9, 17);
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("ImageUsername: {}", imageUsername);
        log.info("Username: {}, Password: {}", username, password);
        var callback = new RequestCallback<Image>() {
            @Override
            public void onRequest(Image entity) {
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
        imageClient.get(imageUsername, spyCallback, authHeader);
        verify(spyCallback, timeout(TIMEOUT_MS)).onRequest(ArgumentMatchers.any(Image.class));
        verify(spyCallback, times(1)).onRequest(ArgumentMatchers.any(Image.class));
        verify(spyCallback, times(0)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(0)).onFail(ArgumentMatchers.any(Throwable.class));
    }

    @DisplayName("Get image with unsuccessful authentication")
    @Test
    void getImageWithUnsuccessfulAuthentication() {
        final var imageUsername = RandomStringUtils.randomAlphabetic(9, 17);
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("ImageUsername: {}", imageUsername);
        log.info("Username: {}, Password: {}", username, password);
        var callback = new RequestCallback<Image>() {
            @Override
            public void onRequest(Image entity) {
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
        imageClient.get(imageUsername, spyCallback, authHeader);
        verify(spyCallback, timeout(TIMEOUT_MS)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(1)).onError(ArgumentMatchers.any(Error.class));
        verify(spyCallback, times(0)).onRequest(ArgumentMatchers.any(Image.class));
        verify(spyCallback, times(0)).onFail(ArgumentMatchers.any(Throwable.class));
    }

}