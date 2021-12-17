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
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.model.UserInfo;
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
class UserClientImplTest {

    static final String URL = "http://localhost:8080/user";
    static final long TIMEOUT_MS_CALL = 3_000L;
    static final long TIMEOUT_MS_EXECUTE = 1_000L;

    UserClientImpl userClient;

    @BeforeEach
    void init() {
        var objectMapper = new ObjectMapper();
        var httpClient = HttpClients.createSystem();
        var executorService = Executors.newSingleThreadExecutor();
        this.userClient = new UserClientImpl(URL, objectMapper, httpClient, executorService);
    }

    @Test
    @DisplayName("Get with successful authentication")
    void getWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var userRef = new AtomicReference<User>();
        var callback = new RequestCallback<User>() {
            @Override
            public void onResponse(User responseBody) {
                log.info("User: {}", responseBody);
                userRef.set(responseBody);
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
        userClient.get(callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onResponse(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var user = userRef.get();
        assertNotNull(user);
        verify(callbackSpy).onResponse(ArgumentMatchers.any());
        verify(callbackSpy, only()).onResponse(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Get with unsuccessful authentication")
    void getWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<User>() {
            @Override
            public void onResponse(User responseBody) {
                log.error("User: {}", responseBody);
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
        userClient.get(callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var error = errRef.get();
        assertNotNull(error);
        verify(callbackSpy).onError(ArgumentMatchers.any());
        verify(callbackSpy, only()).onError(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Get exiting user by id with successful authentication")
    void getExistingUserByIdWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var id = 1;
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var userRef = new AtomicReference<User>();
        var callback = new RequestCallback<User>() {
            @Override
            public void onResponse(User responseBody) {
                log.info("User: {}", responseBody);
                userRef.set(responseBody);
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
        userClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onResponse(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var user = userRef.get();
        assertNotNull(user);
        verify(callbackSpy).onResponse(ArgumentMatchers.any());
        verify(callbackSpy, only()).onResponse(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Get non-existing user by id with successful authentication")
    void getNonExistingUserByIdWithSuccessfulAuthentication() throws InterruptedException {
        final var username = "user";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var id = 9000;
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<User>() {
            @Override
            public void onResponse(User responseBody) {
                log.error("User: {}", responseBody);
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
        userClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var error = errRef.get();
        assertNotNull(error);
        verify(callbackSpy).onError(ArgumentMatchers.any());
        verify(callbackSpy, only()).onError(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Get by id with unsuccessful authentication")
    void getByIdWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        log.info("Username: {}, Password: {}", username, password);
        final var id = 1;
        log.info("Id: {}", id);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<User>() {
            @Override
            public void onResponse(User responseBody) {
                log.error("User: {}", responseBody);
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
        userClient.get(id, callbackSpy, authHeader);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onError(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var error = errRef.get();
        assertNotNull(error);
        verify(callbackSpy).onError(ArgumentMatchers.any());
        verify(callbackSpy, only()).onError(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Save validated user")
    void saveValidatedUser() throws InterruptedException {
        var username = RandomStringUtils.randomAlphabetic(7, 11);
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = RandomStringUtils.randomAlphabetic(7, 11);
        var lastname = RandomStringUtils.randomAlphabetic(7, 11);
        var userInfo = new UserInfo(0, firstname, lastname, 0L);
        var user = new User(0, username, email, password, userInfo);
        log.info("User: {}", user);
        var latch = new CountDownLatch(1);
        var userRef = new AtomicReference<User>();
        var callback = new RequestCallback<User>() {
            @Override
            public void onResponse(User responseBody) {
                log.info("User: {}", responseBody);
                userRef.set(responseBody);
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
        userClient.save(user, callbackSpy);
        verify(callbackSpy, timeout(TIMEOUT_MS_CALL)).onResponse(ArgumentMatchers.any());
        var completed = latch.await(TIMEOUT_MS_EXECUTE, TimeUnit.MILLISECONDS);
        if (!completed)
            fail(new IllegalStateException("Callback execution timed out"));
        var responseUser = userRef.get();
        assertNotNull(responseUser);
        verify(callbackSpy).onResponse(ArgumentMatchers.any());
        verify(callbackSpy, only()).onResponse(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Save invalidated user")
    void saveInvalidUser() throws InterruptedException {
        var username = "user-name";
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = RandomStringUtils.randomAlphabetic(7, 11);
        var lastname = RandomStringUtils.randomAlphabetic(7, 11);
        var userInfo = new UserInfo(0, firstname, lastname, 0L);
        var user = new User(0, username, email, password, userInfo);
        log.info("User: {}", user);
        var latch = new CountDownLatch(1);
        var errRef = new AtomicReference<Error>();
        var callback = new RequestCallback<User>() {
            @Override
            public void onResponse(User responseBody) {
                log.error("User: {}", responseBody);
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
        userClient.save(user, callbackSpy);
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