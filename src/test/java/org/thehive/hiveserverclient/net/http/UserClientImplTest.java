package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.fail;

class UserClientImplTest {

    UserClientImpl userClient;

    @BeforeEach
    void initialize() {
        var url = "http://localhost:8080/user";
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.userClient = new UserClientImpl(url, httpClient, objectMapper, threadPoolExecutor);
    }

    @DisplayName("Get with successful authentication")
    @Test
    void getWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        userClient.get(new RequestCallback<User>() {
            @Override
            public void onRequest(User data) {
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                fail(e.getMessage());
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }

    @DisplayName("Get with unsuccessful authentication")
    @Test
    void getWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        userClient.get(new RequestCallback<User>() {
            @Override
            public void onRequest(User data) {
                fail("Login is successful");
                latch.countDown();
            }

            @Override
            public void onError(Error e) {
                latch.countDown();
            }

            @Override
            public void onFail(Throwable t) {
                fail(t);
                latch.countDown();
            }
        }, authHeader);
        latch.await();
    }



}