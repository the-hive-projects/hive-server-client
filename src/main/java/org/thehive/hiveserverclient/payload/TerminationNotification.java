package org.thehive.hiveserverclient.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminationNotification implements Payload {

    private long timestamp;

}
