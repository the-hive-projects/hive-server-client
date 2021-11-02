package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.service.result.LoginResult;

import java.util.function.Consumer;

public interface UserService {

    void signIn(String username, String password, Consumer<? super LoginResult> consumer);

}
