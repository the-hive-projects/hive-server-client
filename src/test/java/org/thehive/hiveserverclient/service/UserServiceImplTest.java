package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.net.http.UserClientImpl;
import org.thehive.hiveserverclient.service.result.LoginResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceImplTest {

    UserServiceImpl userService;

    @BeforeEach
    void initialize() {
        var url = "http://localhost:8080/user";
        var httpClient = HttpClients.createSystem();
        var objectMapper = new ObjectMapper();
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        var userClient = new UserClientImpl(url, httpClient, objectMapper, threadPoolExecutor);
        this.userService = new UserServiceImpl(userClient);
    }

    @DisplayName("Sign-in with correct credentials")
    @Test
    void signInWithCorrectCredentials() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var latch = new CountDownLatch(1);
        userService.signIn(username, password, result -> {
            assertEquals(LoginResult.Status.SUCCESSFUL, result.status);
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Sign-in with incorrect credentials")
    @Test
    void signInWithIncorrectCredentials() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var latch = new CountDownLatch(1);
        userService.signIn(username, password, result -> {
            assertEquals(LoginResult.Status.UNSUCCESSFUL, result.status);
            latch.countDown();
        });
        latch.await();
    }


}