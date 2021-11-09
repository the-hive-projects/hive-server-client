package org.thehive.hiveserverclient.service;

import lombok.*;
import org.thehive.hiveserverclient.model.Entity;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ResultImpl<S extends Enum<?>, E extends Entity> implements Result<S, E> {

    private final S status;
    private final E entity;
    private final String message;
    private final Throwable exception;

    @Override
    public S status() {
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
