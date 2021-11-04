package org.thehive.hiveserverclient.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User implements Entity {

    private int id;
    private String username;
    private String email;
    private String password;
    private UserInfo userInfo;

}
