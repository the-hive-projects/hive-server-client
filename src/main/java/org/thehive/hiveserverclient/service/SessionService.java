package org.thehive.hiveserverclient.service;


import org.thehive.hiveserverclient.model.Session;

import java.util.function.Consumer;

public interface SessionService {

    void takeAll(Consumer<? super AppResponse<? extends Session[]>> consumer);

    void takeLive(String liveId, Consumer<? super AppResponse<? extends Session>> consumer);

    void create(Session session, Consumer<? super AppResponse<? extends Session>> consumer);

}
