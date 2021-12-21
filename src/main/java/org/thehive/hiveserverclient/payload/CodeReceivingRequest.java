package org.thehive.hiveserverclient.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeReceivingRequest implements Payload {

    private String broadcaster;
    private String receiver;
    private boolean start;

}
