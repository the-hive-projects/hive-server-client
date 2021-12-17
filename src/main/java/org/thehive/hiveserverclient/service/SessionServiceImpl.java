package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.SessionClient;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionClient sessionClient;

    public SessionServiceImpl(@NonNull SessionClient sessionClient) {
        this.sessionClient = sessionClient;
    }

    @Override
    public void take(@NonNull int id, @NonNull Consumer<? super AppResponse<? extends Session>> consumer) {
        log.info("#take id: {}", id);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        sessionClient.get(id, new RequestCallback<>() {
            @Override
            public void onResponse(Session responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#take id: {}, status: {}", id, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<Session> response;
                if (error.getStatus() == 404)
                    response = AppResponse.of(ResponseStatus.ERROR_UNAVAILABLE, error.getMessage());
                else
                    response = AppResponse.of(error.getMessage());
                log.info("#take id: {}, status: {}", id, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Session>of(t);
                log.info("#take id: {}, status: {}", id, response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void takeLive(String liveId, Consumer<? super AppResponse<? extends Session>> consumer) {
        log.info("#takeLive liveId: {}", liveId);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        sessionClient.getLive(liveId, new RequestCallback<>() {
            @Override
            public void onResponse(Session responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#take liveId: {}, status: {}", liveId, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<Session> response;
                if (error.getStatus() == 404)
                    response = AppResponse.of(ResponseStatus.ERROR_UNAVAILABLE, error.getMessage());
                else
                    response = AppResponse.of(error.getMessage());
                log.info("#take liveId: {}, status: {}", liveId, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Session>of(t);
                log.info("#take liveId: {}, status: {}", liveId, response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void create(@NonNull Session session, @NonNull Consumer<? super AppResponse<? extends Session>> consumer) {
        log.info("#create session: {}", session);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        sessionClient.save(session, new RequestCallback<>() {
            @Override
            public void onResponse(Session responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#create session: {}, status: {}", session, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                var response = AppResponse.<Session>of(error.getMessage());
                log.info("#create session: {}, status: {}", session, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Session>of(t);
                log.info("#create session: {}, status: {}", session, response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
