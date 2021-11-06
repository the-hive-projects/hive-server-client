package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Image;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.fail;

// Run this test while server is up.
@Slf4j
class ImageClientImplTest {

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
    void getImageWithSuccessfulAuthentication() throws InterruptedException {
        final var imageUsername = "username";
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        log.info("ImageUsername: {}",imageUsername);
        imageClient.get(imageUsername, new RequestCallback<>() {
            @Override
            public void onRequest(Image entity) {
                log.info("Entity: {}", entity);
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                log.warn("Error: {}", e);
                fail(e.getMessage());
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }

    @DisplayName("Get image with unsuccessful authentication")
    @Test
    void getImageWithUnsuccessfulAuthentication() throws InterruptedException {
        final var imageUsername = "username";
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        imageClient.get(imageUsername, new RequestCallback<>() {
            @Override
            public void onRequest(Image entity) {
                log.warn("Entity: {}", entity);
                fail("'get' request must get error when authentication is unsuccessful");
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                log.info("Error: {}", e);
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                log.warn(t.getMessage());
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }

}