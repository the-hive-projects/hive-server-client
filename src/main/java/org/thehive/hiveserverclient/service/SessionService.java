package org.thehive.hiveserverclient.service;


import org.thehive.hiveserverclient.model.Session;

import java.util.function.Consumer;

public interface SessionService {

    void take(String id, Consumer<? super Result<Status,? extends Session>> consumer);

    void create(Session session, Consumer<? super Result<Status,? extends Session>> consumer);

}
