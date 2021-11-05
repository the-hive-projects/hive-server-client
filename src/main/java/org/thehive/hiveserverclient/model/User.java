package org.thehive.hiveserverclient.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Entity {

    private Integer id;
    private String username;
    private String email;
    private String password;
    private UserInfo userInfo;

}
