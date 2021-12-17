package org.thehive.hiveserverclient.service;

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
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.model.UserInfo;
import org.thehive.hiveserverclient.net.http.UserClientImpl;
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
class UserServiceImplTest {

    static final String URL = "http://localhost:8080/user";
    static final long TIMEOUT_MS_CALL = 3_000L;
    static final long TIMEOUT_MS_EXECUTE = 1_000L;

    UserServiceImpl userService;

    @BeforeEach
    void initialize() {
        var objectMapper = new ObjectMapper();
        var httpClient = HttpClients.createSystem();
        var threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        var userClient = new UserClientImpl(URL, objectMapper, httpClient, threadPoolExecutor);
        this.userService = new UserServiceImpl(userClient);
    }

    @BeforeEach
    void unauthenticate() {
        Authentication.INSTANCE.unauthenticate();
    }

    @Test
    @DisplayName("Sign-in with correct credentials")
    void signInWithCorrectCredentials() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.signIn(username, password, consumerSpy);
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
    @DisplayName("Sign-in with incorrect credentials")
    void signInWithIncorrectCredentials() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.signIn(username, password, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResponseStatus.ERROR_INCORRECT, result.status());
        verify(consumerSpy).accept(ArgumentMatchers.any());
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Sign-up with invalid credentials")
    void signUpWithValidCredentials() throws InterruptedException {
        var username = RandomStringUtils.randomAlphabetic(7, 11);
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = RandomStringUtils.randomAlphabetic(7, 11);
        var lastname = RandomStringUtils.randomAlphabetic(7, 11);
        var userInfo = new UserInfo(0, firstname, lastname, 0L);
        var user = new User(0, username, email, password, userInfo);
        log.info("User: {}", user);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.signUp(user, consumerSpy);
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
    @DisplayName("Sign-up with invalid credentials")
    void signUpWithInvalidCredentials() throws InterruptedException {
        var username = "user-name";
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = RandomStringUtils.randomAlphabetic(7, 11);
        var lastname = RandomStringUtils.randomAlphabetic(7, 11);
        var userInfo = new UserInfo(0, firstname, lastname, 0L);
        var user = new User(0, username, email, password, userInfo);
        log.info("User: {}", user);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.signUp(user, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResponseStatus.ERROR_INVALID, result.status());
        verify(consumerSpy).accept(ArgumentMatchers.any());
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Profile when authentication is correct")
    void profileWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.profile(consumerSpy);
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
    @DisplayName("Profile when authentication is incorrect")
    void profileWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.profile(consumerSpy);
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

    @Test
    @DisplayName("Profile existing user with id when authentication is correct")
    void profileExistingUserWithIdWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var id = 1;
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.profile(id, consumerSpy);
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
    @DisplayName("Profile with non-existing user id when authentication is correct")
    void profileWithNonExistingUserIdWhenAuthenticationIsCorrect() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var id = 9000;
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.profile(id, consumerSpy);
        verify(consumerSpy, timeout(TIMEOUT_MS_CALL)).accept(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var result = resultRef.get();
        assertNotNull(result);
        assertEquals(ResponseStatus.ERROR_UNAVAILABLE, result.status());
        verify(consumerSpy).accept(ArgumentMatchers.any());
        verify(consumerSpy, only()).accept(ArgumentMatchers.any());
    }

    @DisplayName("Profile with id when authentication is incorrect")
    @Test
    void profileWithIdWhenAuthenticationIsIncorrect() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var token = HeaderUtils.httpBasicAuthenticationToken(username, password);
        Authentication.INSTANCE.authenticate(token);
        log.info("Username: {}, Password: {}", username, password);
        final var id = 1;
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var resultRef = new AtomicReference<AppResponse<? extends User>>();
        var consumer = new Consumer<AppResponse<? extends User>>() {
            @Override
            public void accept(AppResponse<? extends User> result) {
                log.info("AppResponse: {}", result);
                resultRef.set(result);
                latch.countDown();
            }
        };
        var consumerSpy = spy(consumer);
        userService.profile(id, consumerSpy);
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