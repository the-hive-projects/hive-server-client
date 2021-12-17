package org.thehive.hiveserverclient.service;

import java.util.Optional;

public interface AppResponse<R> {

    static <R> AppResponse<R> of(R result) {
        return AppResponseImpl.<R>builder()
                .status(ResponseStatus.SUCCESS)
                .response(result)
                .build();
    }

    static <R> AppResponse<R> of(ResponseStatus status, String message) {
        return AppResponseImpl.<R>builder()
                .status(status)
                .message(message)
                .build();
    }

    static <R> AppResponse<R> of(String message) {
        return AppResponseImpl.<R>builder()
                .status(ResponseStatus.ERROR)
                .message(message)
                .build();
    }

    static <R> AppResponse<R> of(Throwable exception) {
        return AppResponseImpl.<R>builder()
                .status(ResponseStatus.FAIL)
                .exception(exception)
                .build();
    }

    ResponseStatus status();

    Optional<R> response();

    Optional<String> message();

    Optional<Throwable> exception();

}
