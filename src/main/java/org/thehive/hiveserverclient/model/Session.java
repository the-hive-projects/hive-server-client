package org.thehive.hiveserverclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Entity {

    private Integer id;
    private String name;
    private String liveId;
    private Long duration;
    private User user;
    private Long creationTime;

}
