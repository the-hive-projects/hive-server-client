package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.model.UserInfo;
import org.thehive.hiveserverclient.net.http.UserClientImpl;
import org.thehive.hiveserverclient.test.util.StringUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Run this test while server is up.
@Slf4j
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
        log.info("Username: {}, Password: {}", username, password);
        userService.signIn(username, password, result -> {
            log.info("Result: {}", result);
            assertEquals(SignInStatus.CORRECT, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
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
        log.info("Username: {}, Password: {}", username, password);
        userService.signIn(username, password, result -> {
            log.info("Result: {}", result);
            assertEquals(SignInStatus.INCORRECT, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Sign-up with invalid credentials")
    @Test
    void signUpWithValidCredentials() throws InterruptedException {
        var username = StringUtils.randomAlphabeticString(11);
        var password = "password";
        var email = StringUtils.randomAlphabeticString(13) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, password, email, new UserInfo(0, firstname, lastname, 0));
        var latch = new CountDownLatch(1);
        userService.signUp(user, result -> {
            log.info("Result: {}", result);
            assertEquals(SignUpStatus.VALID, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Sign-up with invalid credentials")
    @Test
    void signUpWithInvalidCredentials() throws InterruptedException {
        var username = "user-name";
        var password = "password";
        var email = StringUtils.randomAlphabeticString(13) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, password, email, new UserInfo(0, firstname, lastname, 0));
        var latch = new CountDownLatch(1);
        userService.signUp(user, result -> {
            log.info("Result: {}", result);
            assertEquals(SignUpStatus.INVALID, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }


}