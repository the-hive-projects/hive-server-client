package org.thehive.hiveserverclient.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveNotification implements Payload {

    private String username;
    private long timestamp;

}
