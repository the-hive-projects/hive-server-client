package org.thehive.hiveserverclient.net.websocket.header;

import lombok.NonNull;
import org.thehive.hiveserverclient.payload.Chat;
import org.thehive.hiveserverclient.payload.Information;
import org.thehive.hiveserverclient.payload.Payload;

public enum PayloadType {

    INFORMATION(Information.class, "information"),
    CHAT(Chat.class, "chat");

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
