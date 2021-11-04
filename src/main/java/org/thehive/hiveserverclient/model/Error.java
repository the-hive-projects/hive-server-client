package org.thehive.hiveserverclient.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    private long timestamp;
    private int status;
    private String error;
    private String exception;
    private String message;
    private String path;
    private String method;

}
