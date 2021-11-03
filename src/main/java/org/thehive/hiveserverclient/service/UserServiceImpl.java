package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.message.BasicHeader;
import org.thehive.hiveserverclient.Session;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.UserClient;
import org.thehive.hiveserverclient.service.result.LoginResult;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    protected final UserClient userClient;

    @Override
    public void signIn(@NonNull String username, @NonNull String password, @NonNull Consumer<? super LoginResult> consumer) {
        var authHeader = new BasicHeader(HeaderUtils.HTTP_BASIC_AUTHENTICATION_HEADER_NAME, HeaderUtils.httpBasicAuthenticationToken(username, password));
        userClient.get(new RequestCallback<User>() {
            @Override
            public void onRequest(User data) {
                Session.SESSION.authenticate(authHeader.getValue());
                Session.SESSION.addArgument("header", authHeader);
                consumer.accept(LoginResult.successfulOf(data));
            }

            @Override
            public void onError(Error e) {
                consumer.accept(LoginResult.unsuccessfulOf(e.getMessage()));
            }

            @Override
            public void onFail(Throwable t) {
                consumer.accept(LoginResult.failedOf(t));
            }
        }, authHeader);
    }

}
