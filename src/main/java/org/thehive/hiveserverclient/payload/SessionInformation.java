package org.thehive.hiveserverclient.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionInformation implements Payload {

    private long timestamp;
    private String ownerUsername;
    private List<String> participantUsernameList;

}
