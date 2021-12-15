package org.thehive.hiveserverclient.service;


import org.thehive.hiveserverclient.model.Session;

import java.util.function.Consumer;

public interface SessionService {

    void take(int id, Consumer<? super Result<? extends Session>> consumer);

    void takeLive(String liveId, Consumer<? super Result<? extends Session>> consumer);

    void create(Session session, Consumer<? super Result<? extends Session>> consumer);

}
