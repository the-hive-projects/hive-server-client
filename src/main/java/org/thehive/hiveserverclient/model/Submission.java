package org.thehive.hiveserverclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission implements Entity {

    public static final Submission EMPTY = new Submission();

    private int id;
    private String content;
    private Session session;
    private User user;
    private long creationTime;

}
