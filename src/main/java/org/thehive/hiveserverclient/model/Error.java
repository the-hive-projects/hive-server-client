package org.thehive.hiveserverclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
