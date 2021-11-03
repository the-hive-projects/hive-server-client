package org.thehive.hiveserverclient.net.http;

import org.thehive.hiveserverclient.model.Error;

public interface RequestCallback<E> {

    void onRequest(E entity);

    void onError(Error e);

    void onFail(Throwable t);

}
