package org.thehive.hiveserverclient.net.http;

public interface RequestCallback<D> {

    void onRequest(D data);

    void onError(Error e);

    void onFail(Throwable t);

}
