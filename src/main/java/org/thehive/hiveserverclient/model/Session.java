package org.thehive.hiveserverclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Entity {

    private String id;
    private String name;
    private User createdBy;
    private Long createdAt;

}
