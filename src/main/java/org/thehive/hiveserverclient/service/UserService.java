package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.service.status.ProfileStatus;
import org.thehive.hiveserverclient.service.status.SignInStatus;
import org.thehive.hiveserverclient.service.status.SignUpStatus;

import java.util.function.Consumer;

public interface UserService {

    void signIn(String username, String password, Consumer<? super Result<SignInStatus, ? extends User>> consumer);

    void signUp(User user, Consumer<? super Result<SignUpStatus, ? extends User>> consumer);

    void profile(Consumer<? super Result<ProfileStatus, ? extends User>> consumer);

    void profile(int id, Consumer<? super Result<ProfileStatus, ? extends User>> consumer);

}
