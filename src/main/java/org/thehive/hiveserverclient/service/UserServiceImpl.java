package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.UserClient;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@Slf4j
public class UserServiceImpl implements UserService {

    private final UserClient userClient;

    public UserServiceImpl(@NonNull UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public void signIn(@NonNull String username, @NonNull String password, @NonNull Consumer<? super AppResponse<? extends User>> consumer) {
        log.info("#signIn username: {}, password: {}", username, password);
        if (Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has already been authenticated");
        var authenticationHeader = new BasicHeader(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, HeaderUtils.httpBasicAuthenticationToken(username, password));
        userClient.get(new RequestCallback<>() {
            @Override
            public void onResponse(User responseBody) {
                Authentication.INSTANCE.authenticate(authenticationHeader.getValue());
                var response = AppResponse.of(responseBody);
                log.info("#signIn username: {}, password: {}, status: {}", username, password, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<User> response;
                if (error.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                    response = AppResponse.of(ResponseStatus.ERROR_INCORRECT, error.getMessage());
                } else {
                    response = AppResponse.of(error.getMessage());
                }
                log.info("#signIn username: {}, password: {}, status: {}", username, password, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<User>of(t);
                log.info("#signIn username: {}, password: {}, status: {}", username, password, response.status().name());
                consumer.accept(response);
            }
        }, authenticationHeader);
    }

    @Override
    public void signUp(@NonNull User user, @NonNull Consumer<? super AppResponse<? extends User>> consumer) {
        log.info("#signUp user: {}", user);
        if (Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has already been authenticated");
        userClient.save(user, new RequestCallback<>() {
            @Override
            public void onResponse(User responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#signUp user: {}, status: {}", user, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<User> response;
                if (error.getStatus() == HttpStatus.SC_BAD_REQUEST) {
                    response = AppResponse.of(ResponseStatus.ERROR_INVALID, error.getMessage());
                } else {
                    response = AppResponse.of(error.getMessage());
                }
                log.info("#signUp user: {}, status: {}", user, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<User>of(t);
                log.info("#signUp user: {}, status: {}", user, response.status().name());
                consumer.accept(response);
            }
        });
    }

    @Override
    public void profile(@NonNull Consumer<? super AppResponse<? extends User>> consumer) {
        log.info("#profile");
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        userClient.get(new RequestCallback<>() {
            @Override
            public void onResponse(User responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#profile status: {}", response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                var response = AppResponse.<User>of(error.getMessage());
                log.info("#profile status: {}", response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<User>of(t);
                log.info("#profile status: {}", response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void profile(int id, @NonNull Consumer<? super AppResponse<? extends User>> consumer) {
        log.info("#profile id: {}", id);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        userClient.get(id, new RequestCallback<>() {
            @Override
            public void onResponse(User responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#profile id: {}, status: {}", id, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<User> response;
                if (error.getStatus() == HttpStatus.SC_NOT_FOUND) {
                    response = AppResponse.of(ResponseStatus.ERROR_UNAVAILABLE, error.getMessage());
                } else {
                    response = AppResponse.of(error.getMessage());
                }
                log.info("#profile id: {}, status: {}", id, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<User>of(t);
                log.info("#profile id: {}, status: {}", id, response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
