package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.model.User;

import java.util.function.Consumer;

public interface UserService {

    void signIn(String username, String password, Consumer<? super AppResponse<? extends User>> consumer);

    void signUp(User user, Consumer<? super AppResponse<? extends User>> consumer);

    void profile(Consumer<? super AppResponse<? extends User>> consumer);

    void profile(int id, Consumer<? super AppResponse<? extends User>> consumer);

}
