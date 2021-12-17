package org.thehive.hiveserverclient.service;

import lombok.*;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class AppResponseImpl<R> implements AppResponse<R> {

    private final ResponseStatus status;
    private final R response;
    private final String message;
    private final Throwable exception;

    @Override
    public ResponseStatus status() {
        return status;
    }

    @Override
    public Optional<R> response() {
        return Optional.ofNullable(response);
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
