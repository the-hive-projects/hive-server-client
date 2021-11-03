package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.model.User;

import java.util.function.Consumer;

public interface UserService {

    void signIn(String username, String password, Consumer<? super Result<SignInStatus, User>> consumer);

    void signUp(User user,Consumer<? super Result<SignUpStatus, User>> consumer);

}
