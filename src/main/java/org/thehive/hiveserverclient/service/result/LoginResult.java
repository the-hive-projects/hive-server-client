package org.thehive.hiveserverclient.service.result;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.thehive.hiveserverclient.model.User;

import java.util.Optional;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginResult implements Result {

    public final Status status;
    public final Optional<User> user;
    public final Optional<String> message;
    public final Optional<Throwable> exception;

    public static LoginResult successfulOf(@NonNull User user) {
        return LoginResult.builder()
                .status(Status.SUCCESSFUL)
                .user(Optional.of(user))
                .message(Optional.empty())
                .exception(Optional.empty())
                .build();
    }

    public static LoginResult unsuccessfulOf(@NonNull String message) {
        return LoginResult.builder()
                .status(Status.UNSUCCESSFUL)
                .message(Optional.of(message))
                .user(Optional.empty())
                .exception(Optional.empty())
                .build();
    }

    public static LoginResult failedOf(@NonNull Throwable exception) {
        return LoginResult.builder()
                .status(Status.FAILED)
                .exception(Optional.of(exception))
                .user(Optional.empty())
                .message(Optional.empty())
                .build();
    }

    public enum Status {
        SUCCESSFUL,
        UNSUCCESSFUL,
        FAILED;
    }

}
