package org.thehive.hiveserverclient.net.http;

import org.thehive.hiveserverclient.model.Error;

public interface RequestCallback<R> {

    void onResponse(R responseBody);

    void onError(Error error);

    void onFail(Throwable t);

}
