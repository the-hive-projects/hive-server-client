package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.payload.Chat;
import org.thehive.hiveserverclient.payload.Information;
import org.thehive.hiveserverclient.payload.Payload;

public interface SessionWebSocketListener {

    void onInformation(Information information);

    void onChat(Chat chat);

    default void onConnect() {
    }

    default void onSend(Payload payload) {
    }

    default void onException(Throwable t) {
    }

    default void onDisconnect() {
    }

}
