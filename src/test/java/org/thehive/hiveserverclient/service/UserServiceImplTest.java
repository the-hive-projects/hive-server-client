package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.model.UserInfo;
import org.thehive.hiveserverclient.net.http.UserClientImpl;
import org.thehive.hiveserverclient.util.HeaderUtils;

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

    @BeforeEach
    void unauthenticate() {
        Authentication.INSTANCE.unauthenticate();
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
            assertEquals(Status.CORRECT, result.status());
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
            assertEquals(Status.INCORRECT, result.status());
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
        var username = RandomStringUtils.randomAlphabetic(7, 11);
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, email, password, new UserInfo(0, firstname, lastname, 0L));
        var latch = new CountDownLatch(1);
        log.info("User: {}", user);
        userService.signUp(user, result -> {
            log.info("Result: {}", result);
            assertEquals(Status.VALID, result.status());
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
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, email, password, new UserInfo(0, firstname, lastname, 0L));
        var latch = new CountDownLatch(1);
        log.info("User: {}", user);
        userService.signUp(user, result -> {
            log.info("Result: {}", result);
            assertEquals(Status.INVALID, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Profile when authentication is correct")
    @Test
    void profileWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        userService.profile(result -> {
            log.info("Result: {}", result);
            assertEquals(Status.TAKEN, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Profile when authentication is incorrect")
    @Test
    void profileWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        userService.profile(result -> {
            log.info("Result: {}", result);
            assertEquals(Status.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Profile with id when authentication is correct")
    @Test
    void profileWithIdWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        userService.profile(1, result -> {
            log.info("Result: {}", result);
            assertEquals(Status.TAKEN, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Profile with id when authentication is incorrect")
    @Test
    void profileWithIdWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        userService.profile(1, result -> {
            log.info("Result: {}", result);
            assertEquals(Status.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }


}