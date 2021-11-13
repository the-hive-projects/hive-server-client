package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import org.thehive.hiveserverclient.net.websocket.ConnectionStatus;
import org.thehive.hiveserverclient.net.websocket.WebSocketClient;
import org.thehive.hiveserverclient.net.websocket.WebSocketListener;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class WebSocketSingleConnServiceImpl extends WebSocketServiceImpl implements WebSocketSingleConnService {

    protected final AtomicReference<WebSocketSingleSubConnection> connectionReference;

    protected WebSocketSingleConnServiceImpl(@NonNull WebSocketClient webSocketClient) {
        super(webSocketClient);
        this.connectionReference = new AtomicReference<>();
    }

    @Override
    public WebSocketSingleSubConnection connect(WebSocketListener listener) {
        if (hasConnection())
            return connectionReference.get();
        var conn = super.connect(listener);
        var singleSubConn = new WebSocketSingleSubConnectionImpl(conn);
        connectionReference.set(singleSubConn);
        return singleSubConn;
    }

    @Override
    public boolean hasConnection() {
        return connectionReference.get() != null && connectionReference.get().status() != ConnectionStatus.DISCONNECTED;
    }

    @Override
    public Optional<WebSocketSingleSubConnection> getConnection() {
        return hasConnection() ? Optional.of(connectionReference.get()) : Optional.empty();
    }

}
