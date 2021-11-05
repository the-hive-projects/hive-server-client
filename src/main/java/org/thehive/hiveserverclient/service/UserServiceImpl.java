package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.thehive.hiveserverclient.Session;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.UserClient;
import org.thehive.hiveserverclient.service.result.Result;
import org.thehive.hiveserverclient.service.status.ProfileStatus;
import org.thehive.hiveserverclient.service.status.SignInStatus;
import org.thehive.hiveserverclient.service.status.SignUpStatus;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    protected final UserClient userClient;

    @Override
    public void signIn(@NonNull String username, @NonNull String password, @NonNull Consumer<? super Result<SignInStatus, ? extends User>> consumer) {
        var authHeader = new BasicHeader(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, HeaderUtils.httpBasicAuthenticationToken(username, password));
        userClient.get(new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                Session.SESSION.authenticate(authHeader.getValue());
                Session.SESSION.addArgument("header", authHeader);
                var result = Result.of(SignInStatus.CORRECT, entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error e) {
                Result<SignInStatus, User> result;
                if (e.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                    result = Result.of(SignInStatus.INCORRECT, e.getMessage());
                } else {
                    result = Result.of(SignInStatus.ERROR, e.getMessage());
                }
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<SignInStatus, User>of(SignInStatus.FAIL, t);
                consumer.accept(result);
            }
        }, authHeader);
    }

    @Override
    public void signUp(User user, Consumer<? super Result<SignUpStatus, ? extends User>> consumer) {
        userClient.save(user, new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                var result = Result.of(SignUpStatus.VALID, entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error e) {
                Result<SignUpStatus, User> result;
                if (e.getStatus() == HttpStatus.SC_BAD_REQUEST) {
                    result = Result.of(SignUpStatus.INVALID, e.getMessage());
                } else {
                    result = Result.of(SignUpStatus.ERROR, e.getMessage());
                }
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<SignUpStatus, User>of(SignUpStatus.FAIL, t);
                consumer.accept(result);
            }
        });
    }

    @Override
    public void profile(Consumer<? super Result<ProfileStatus, ? extends User>> consumer) {
        userClient.get(new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                var result = Result.of(ProfileStatus.TAKEN, entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error e) {
                var result = Result.<ProfileStatus, User>of(ProfileStatus.ERROR, e.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<ProfileStatus, User>of(ProfileStatus.FAIL, t);
                consumer.accept(result);
            }
        }, Session.SESSION.getArgument("header",Header.class));
    }

    @Override
    public void profile(int id, Consumer<? super Result<ProfileStatus, ? extends User>> consumer) {
        userClient.get(id, new RequestCallback<>() {
            @Override
            public void onRequest(User entity) {
                var result = Result.of(ProfileStatus.TAKEN, entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error e) {
                var result = Result.<ProfileStatus, User>of(ProfileStatus.ERROR, e.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<ProfileStatus, User>of(ProfileStatus.FAIL, t);
                consumer.accept(result);
            }
        }, Session.SESSION.getArgument("header", Header.class));
    }

}
