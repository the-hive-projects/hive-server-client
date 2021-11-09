package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.model.UserInfo;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.fail;

// Run this test while server is up.
@Slf4j
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
        log.info("Username: {}, Password: {}", username, password);
        userClient.get(new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
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

    @DisplayName("Get with unsuccessful authentication")
    @Test
    void getWithUnsuccessfulAuthentication() throws InterruptedException {
        final var username = "username";
        final var password = "password";
        var authHeader = HeaderUtils.httpBasicAuthenticationHeader(username, password);
        var latch = new CountDownLatch(1);
        log.info("Username: {}, Password: {}", username, password);
        userClient.get(new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
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

    @DisplayName("Save validated user")
    @Test
    void saveValidatedUser() throws InterruptedException {
        var username = RandomStringUtils.randomAlphabetic(7, 11);
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, email, password, new UserInfo(0, firstname, lastname, 0L));
        var latch = new CountDownLatch(1);
        log.info("User: {}", user);
        userClient.save(user, new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
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
        });
        latch.await();
    }

    @DisplayName("Save invalidated user")
    @Test
    void saveInvalidUser() throws InterruptedException {
        var username = "user-name";
        var password = "password";
        var email = RandomStringUtils.randomAlphabetic(9, 17) + "@test.com";
        var firstname = "testFirstname";
        var lastname = "testLastname";
        var user = new User(0, username, email, password, new UserInfo(0, firstname, lastname, 0L));
        var latch = new CountDownLatch(1);
        log.info("User: {}", user);
        userClient.save(user, new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                log.warn("Entity: {}", entity);
                fail("'save' invalidated user was successful");
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
        });
        latch.await();
    }

}