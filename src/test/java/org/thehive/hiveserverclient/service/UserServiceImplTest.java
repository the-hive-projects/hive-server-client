package org.thehive.hiveserverclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.Session;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.model.UserInfo;
import org.thehive.hiveserverclient.net.http.UserClientImpl;
import org.thehive.hiveserverclient.service.status.ProfileStatus;
import org.thehive.hiveserverclient.service.status.SignInStatus;
import org.thehive.hiveserverclient.service.status.SignUpStatus;
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
    void resetSession() {
        Session.SESSION.unauthenticate();
        Session.SESSION.clear();
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
        var username = RandomStringUtils.randomAlphabetic(7,11);
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9,17) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, email, password, new UserInfo(0, firstname, lastname, 0L));
        var latch = new CountDownLatch(1);
        log.info("User: {}", user);
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
        var email = RandomStringUtils.randomAlphabetic(9,17) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, email, password, new UserInfo(0, firstname, lastname, 0L));
        var latch = new CountDownLatch(1);
        log.info("User: {}", user);
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

    @DisplayName("Profile after session is authenticated")
    @Test
    void profileAfterSessionIsAuthenticated() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Session.SESSION.authenticate(token);
        Session.SESSION.addArgument("header", HeaderUtils.httpBasicAuthenticationHeader(token));
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        userService.profile(result -> {
            log.info("Result: {}", result);
            assertEquals(ProfileStatus.TAKEN, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Profile when session is not authenticated")
    @Test
    void profileWhenSessionIsNotAuthenticated() throws InterruptedException {
        var latch = new CountDownLatch(1);
        userService.profile(result -> {
            log.info("Result: {}", result);
            assertEquals(ProfileStatus.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Profile with id after session is authenticated")
    @Test
    void profileWithIdAfterSessionIsAuthenticated() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Session.SESSION.authenticate(token);
        Session.SESSION.addArgument("header", HeaderUtils.httpBasicAuthenticationHeader(token));
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        userService.profile(1, result -> {
            log.info("Result: {}", result);
            assertEquals(ProfileStatus.TAKEN, result.status());
            assertTrue(result.entity().isPresent());
            assertTrue(result.message().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }

    @DisplayName("Profile with id when session is not authenticated")
    @Test
    void profileWithIdWhenSessionIsNotAuthenticated() throws InterruptedException {
        var latch = new CountDownLatch(1);
        userService.profile(1, result -> {
            log.info("Result: {}", result);
            assertEquals(ProfileStatus.ERROR, result.status());
            assertTrue(result.message().isPresent());
            assertTrue(result.entity().isEmpty());
            assertTrue(result.exception().isEmpty());
            latch.countDown();
        });
        latch.await();
    }


}