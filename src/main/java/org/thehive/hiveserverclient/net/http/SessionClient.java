package org.thehive.hiveserverclient.net.http;

import org.apache.http.Header;
import org.thehive.hiveserverclient.model.Session;

public interface SessionClient {

    void get(String id, RequestCallback<? super Session> callback, Header... headers);

    void save(Session session, RequestCallback<? super Session> callback, Header... headers);

}
