package org.thehive.hiveserverclient.service.result;

import org.thehive.hiveserverclient.model.Entity;

import java.util.Optional;

public interface Result<S extends Enum<?>, E extends Entity> {

    static <S extends Enum<?>, E extends Entity> Result<S, E> initWithEntity(S status, E entity) {
        return ResultImpl.<S, E>builder()
                .status(status)
                .entity(entity)
                .build();
    }

    static <S extends Enum<?>, E extends Entity> Result<S, E> initWithMessage(S status, String message) {
        return ResultImpl.<S, E>builder()
                .status(status)
                .message(message)
                .build();
    }

    static <S extends Enum<?>, E extends Entity> Result<S, E> initWithException(S status, Throwable exception) {
        return ResultImpl.<S, E>builder()
                .status(status)
                .exception(exception)
                .build();
    }

    S status();

    Optional<E> entity();

    Optional<String> message();

    Optional<Throwable> exception();

}
