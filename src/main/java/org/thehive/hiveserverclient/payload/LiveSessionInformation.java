package org.thehive.hiveserverclient.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveSessionInformation implements Payload {

    private String owner;
    private Set<String> participants;
    private long duration;
    private long createdAt;

}
