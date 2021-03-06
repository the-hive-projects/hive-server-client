package org.thehive.hiveserverclient.net.websocket.header;

import lombok.NonNull;
import org.thehive.hiveserverclient.payload.*;

public enum PayloadType {

    LIVE_SESSION_INFORMATION(LiveSessionInformation.class, "live-session-information"),
    PARTICIPATION_NOTIFICATION(ParticipationNotification.class, "participation-notification"),
    EXPIRATION_NOTIFICATION(ExpirationNotification.class, "expiration-notification"),
    CHAT_MESSAGE(ChatMessage.class, "chat-message"),
    CODE_RECEIVING_REQUEST(CodeReceivingRequest.class, "code-receiving-request"),
    CODE_BROADCASTING_NOTIFICATION(CodeBroadcastingNotification.class, "code-broadcasting-notification"),
    CODE_BROADCASTING_INFORMATION(CodeBroadcastingInformation.class, "code-broadcasting-information");

    public final Class<? extends Payload> type;
    public final String value;

    PayloadType(Class<? extends Payload> type, String value) {
        this.type = type;
        this.value = value;
    }

    public static PayloadType byValue(@NonNull String value) throws IllegalArgumentException {
        for (var pt : PayloadType.values())
            if (pt.value.equals(value))
                return pt;
        throw new IllegalArgumentException("Given value is not supported, value: " + value);
    }

}
