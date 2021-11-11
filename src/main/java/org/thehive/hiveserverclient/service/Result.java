package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.model.Entity;

import java.util.Optional;

public interface Result<E extends Entity> {

    static <E extends Entity> Result<E> of(E entity) {
        return ResultImpl.<E>builder()
                .status(ResultStatus.SUCCESS)
                .entity(entity)
                .build();
    }

    static <E extends Entity> Result<E> of(ResultStatus status, String message) {
        return ResultImpl.<E>builder()
                .status(status)
                .message(message)
                .build();
    }

    static <E extends Entity> Result<E> of(String message) {
        return ResultImpl.<E>builder()
                .status(ResultStatus.ERROR)
                .message(message)
                .build();
    }

    static <E extends Entity> Result<E> of(Throwable exception) {
        return ResultImpl.<E>builder()
                .status(ResultStatus.FAIL)
                .exception(exception)
                .build();
    }

    ResultStatus status();

    Optional<E> entity();

    Optional<String> message();

    Optional<Throwable> exception();

}
