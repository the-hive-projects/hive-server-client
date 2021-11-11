package org.thehive.hiveserverclient.service;

import lombok.*;
import org.thehive.hiveserverclient.model.Entity;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ResultImpl<E extends Entity> implements Result<E> {

    private final ResultStatus status;
    private final E entity;
    private final String message;
    private final Throwable exception;

    @Override
    public ResultStatus status() {
        return status;
    }

    @Override
    public Optional<E> entity() {
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<String> message() {
        return Optional.ofNullable(message);
    }

    @Override
    public Optional<Throwable> exception() {
        return Optional.ofNullable(exception);
    }

}
