package org.thehive.hiveserverclient.net.websocket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Chat implements Payload {

    private String from;
    private String text;
    private long timestamp;

}
