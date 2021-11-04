package org.thehive.hiveserverclient.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private int id;
    private String firstname;
    private String lastname;
    private long createdAt;

}
