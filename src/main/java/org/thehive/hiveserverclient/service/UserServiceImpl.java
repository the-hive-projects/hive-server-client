package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.UserClient;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    protected final UserClient userClient;

    @Override
    public void signIn(@NonNull String username, @NonNull String password, @NonNull Consumer<? super Result<? extends User>> consumer) {
        if (Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has already been authenticated");
        var authenticationHeader = new BasicHeader(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, HeaderUtils.httpBasicAuthenticationToken(username, password));
        userClient.get(new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                Authentication.INSTANCE.authenticate(authenticationHeader.getValue());
                var result = Result.of(entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error error) {
                Result<User> result;
                if (error.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                    result = Result.of(ResultStatus.ERROR_INCORRECT, error.getMessage());
                } else {
                    result = Result.of(error.getMessage());
                }
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<User>of(t);
                consumer.accept(result);
            }
        }, authenticationHeader);
    }

    @Override
    public void signUp(@NonNull User user, @NonNull Consumer<? super Result<? extends User>> consumer) {
        if (Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has already been authenticated");
        userClient.save(user, new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                var result = Result.of(entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error error) {
                Result<User> result;
                if (error.getStatus() == HttpStatus.SC_BAD_REQUEST) {
                    result = Result.of(ResultStatus.ERROR_INVALID, error.getMessage());
                } else {
                    result = Result.of(error.getMessage());
                }
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<User>of(t);
                consumer.accept(result);
            }
        });
    }

    @Override
    public void profile(@NonNull Consumer<? super Result<? extends User>> consumer) {
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        userClient.get(new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                var result = Result.of(entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error error) {
                var result = Result.<User>of(error.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<User>of(t);
                consumer.accept(result);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void profile(int id, @NonNull Consumer<? super Result<? extends User>> consumer) {
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        userClient.get(id, new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                var result = Result.of(entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error error) {
                var result = Result.<User>of(error.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<User>of(t);
                consumer.accept(result);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
