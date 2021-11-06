package org.thehive.hiveserverclient.net.http;

import org.apache.http.Header;
import org.thehive.hiveserverclient.model.Image;

public interface ImageClient {

    void get(String username, RequestCallback<? super Image> callback, Header... headers);

}
