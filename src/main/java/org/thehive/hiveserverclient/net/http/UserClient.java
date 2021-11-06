package org.thehive.hiveserverclient.net.http;

import org.apache.http.Header;
import org.thehive.hiveserverclient.model.User;

public interface UserClient {

    void get(RequestCallback<? super User> callback, Header... headers);

    void get(int id, RequestCallback<? super User> callback, Header... headers);

    void save(User user, RequestCallback<? super User> callback, Header... headers);

    void update(int id, User user, RequestCallback<? super User> callback, Header... headers);

}
