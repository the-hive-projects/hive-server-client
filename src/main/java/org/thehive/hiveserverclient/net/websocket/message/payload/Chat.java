package org.thehive.hiveserverclient.net.websocket.message.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Chat implements Payload {

    private long timestamp;
    private String text;

}
