package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.SessionClient;
import org.thehive.hiveserverclient.service.result.Result;
import org.thehive.hiveserverclient.service.status.CreateSessionStatus;
import org.thehive.hiveserverclient.service.status.TakeSessionStatus;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionClient sessionClient;

    @Override
    public void take(@NonNull String id, @NonNull Consumer<? super Result<TakeSessionStatus, ? extends Session>> consumer) {
        sessionClient.get(id, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                var result = Result.of(TakeSessionStatus.TAKEN, entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error e) {
                Result<TakeSessionStatus, Session> result;
                if (e.getStatus() == 404)
                    result = Result.of(TakeSessionStatus.UNAVAILABLE, e.getMessage());
                else
                    result = Result.of(TakeSessionStatus.ERROR, e.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<TakeSessionStatus, Session>of(TakeSessionStatus.FAIL, t);
                consumer.accept(result);
            }
        }, org.thehive.hiveserverclient.Session.SESSION.getArgument("header", Header.class));
    }

    @Override
    public void create(@NonNull Session session, @NonNull Consumer<? super Result<CreateSessionStatus, ? extends Session>> consumer) {
        sessionClient.save(session, new RequestCallback<>() {
            @Override
            public void onRequest(Session entity) {
                var result = Result.of(CreateSessionStatus.CREATED, entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error e) {
                var result = Result.<CreateSessionStatus, Session>of(CreateSessionStatus.ERROR, e.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<CreateSessionStatus, Session>of(CreateSessionStatus.FAIL, t);
                consumer.accept(result);
            }
        }, org.thehive.hiveserverclient.Session.SESSION.getArgument("header", Header.class));
    }

}
