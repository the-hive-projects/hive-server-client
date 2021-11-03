package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.thehive.hiveserverclient.Session;
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
    public void signIn(@NonNull String username, @NonNull String password, @NonNull Consumer<? super Result<SignInStatus, User>> consumer) {
        var authHeader = new BasicHeader(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, HeaderUtils.httpBasicAuthenticationToken(username, password));
        userClient.get(new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                Session.SESSION.authenticate(authHeader.getValue());
                Session.SESSION.addArgument("header", authHeader);
                var user = Result.initWithEntity(SignInStatus.CORRECT, entity);
                consumer.accept(user);
            }

            @Override
            public void onError(Error e) {
                Result<SignInStatus, User> result;
                if (e.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                    result = Result.initWithMessage(SignInStatus.INCORRECT, e.getMessage());
                } else {
                    result = Result.initWithMessage(SignInStatus.ERROR, e.getMessage());
                }
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<SignInStatus, User>initWithException(SignInStatus.FAIL, t);
                consumer.accept(result);
            }
        }, authHeader);
    }

    @Override
    public void signUp(User user, Consumer<? super Result<SignUpStatus, User>> consumer) {
        userClient.save(user, new RequestCallback<User>() {
            @Override
            public void onRequest(User entity) {
                var user = Result.initWithEntity(SignUpStatus.VALID, entity);
                consumer.accept(user);
            }

            @Override
            public void onError(Error e) {
                Result<SignUpStatus, User> result;
                if (e.getStatus() == HttpStatus.SC_BAD_REQUEST) {
                    result = Result.initWithMessage(SignUpStatus.INVALID, e.getMessage());
                } else {
                    result = Result.initWithMessage(SignUpStatus.ERROR, e.getMessage());
                }
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<SignUpStatus, User>initWithException(SignUpStatus.FAIL, t);
                consumer.accept(result);
            }
        });
    }

}
