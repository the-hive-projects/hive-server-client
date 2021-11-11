package org.thehive.hiveserverclient.service;

public enum ResultStatus {

    SUCCESS(0),
    ERROR_INCORRECT(1),
    ERROR_INVALID(1),
    ERROR_UNAVAILABLE(1),
    ERROR(1),
    FAIL(2);

    // 0 -> success
    // 1 -> error
    // 2 -> fail
    private final int type;

    ResultStatus(int type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return type == 0;
    }

    public boolean isError() {
        return type == 1;
    }

    public boolean isFail() {
        return type == 2;
    }

}
