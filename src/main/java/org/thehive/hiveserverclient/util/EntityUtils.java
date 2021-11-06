package org.thehive.hiveserverclient.util;

import lombok.NonNull;
import org.thehive.hiveserverclient.model.Session;
import org.thehive.hiveserverclient.model.User;

public class EntityUtils {

    public static User userOf(@NonNull String username, @NonNull String email, @NonNull String password) {
        return new User(0, username, email, password, null);
    }

    public static Session sessionOf(@NonNull String name) {
        return new Session(null, name, null, null);
    }

}
