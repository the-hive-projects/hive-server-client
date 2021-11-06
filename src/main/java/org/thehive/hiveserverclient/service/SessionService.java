package org.thehive.hiveserverclient.service;


import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.service.result.Result;
import org.thehive.hiveserverclient.service.status.CreateSessionStatus;
import org.thehive.hiveserverclient.service.status.TakeSessionStatus;

import java.util.function.Consumer;

public interface SessionService {

    void take(String id, Consumer<? super Result<TakeSessionStatus,? extends Session>> consumer);

    void create(Session session, Consumer<? super Result<CreateSessionStatus,? extends Session>> consumer);

}
