package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.SessionClient;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionClient sessionClient;

    @Override
    public void take(@NonNull String id, @NonNull Consumer<? super Result<? extends Session>> consumer) {
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        sessionClient.get(id, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                var result = Result.of(entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error error) {
                Result<Session> result;
                if (error.getStatus() == 404)
                    result = Result.of(ResultStatus.ERROR_UNAVAILABLE, error.getMessage());
                else
                    result = Result.of(error.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<Session>of(t);
                consumer.accept(result);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void create(@NonNull Session session, @NonNull Consumer<? super Result<? extends Session>> consumer) {
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        sessionClient.save(session, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                var result = Result.of(entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error error) {
                var result = Result.<Session>of(error.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<Session>of(t);
                consumer.accept(result);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
