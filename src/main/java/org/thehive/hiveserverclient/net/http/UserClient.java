package org.thehive.hiveserverclient.net.http;

import org.apache.http.Header;
import org.thehive.hiveserverclient.model.User;

public interface UserClient {

    void get(RequestCallback<User> callback, Header... headers);

}
