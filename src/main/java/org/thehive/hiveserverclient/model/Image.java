package org.thehive.hiveserverclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image implements Entity {

    private Integer id;
    private byte[] content;

}
