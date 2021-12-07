package org.thehive.hiveserverclient.net.websocket.header;

import lombok.NonNull;
import org.thehive.hiveserverclient.payload.*;

public enum PayloadType {

    JOIN_NOTIFICATION(JoinNotification.class, "join-notification"),
    LEAVE_NOTIFICATION(LeaveNotification.class, "leave-notification"),
    CHAT_MESSAGE(ChatMessage.class, "chat-message"),
    SESSION_INFORMATION(SessionInformation.class, "session-information"),
    TERMINATION_NOTIFICATION(TerminationNotification.class, "termination-notification");

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
