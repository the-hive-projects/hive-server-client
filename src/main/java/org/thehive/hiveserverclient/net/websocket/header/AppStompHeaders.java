package org.thehive.hiveserverclient.net.websocket.header;

import lombok.NonNull;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class AppStompHeaders extends StompHeadersProxy implements AppHeaders {

    public static final String PAYLOAD_TYPE = "payload-type";

    public AppStompHeaders(@NonNull StompHeaders stompHeaders) {
        super(stompHeaders);
    }

    @Override
    public String getPayloadType() {
        return get(PAYLOAD_TYPE).get(0);
    }

}
