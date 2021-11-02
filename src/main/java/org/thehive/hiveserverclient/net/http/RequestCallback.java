package org.thehive.hiveserverclient.net.http;

import org.thehive.hiveserverclient.model.Error;

public interface RequestCallback<D> {

    void onRequest(D data);

    void onError(Error e);

    void onFail(Throwable t);

}
