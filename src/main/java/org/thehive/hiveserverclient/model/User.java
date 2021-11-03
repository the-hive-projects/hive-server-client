package org.thehive.hiveserverclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Entity {

    private int id;
    private String username;
    private String password;
    private String email;
    private UserInfo userInfo;

}
