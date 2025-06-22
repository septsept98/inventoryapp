package com.septian.inventoryapp.model.dto;

public class ErrorException extends RuntimeException {

    private final String message;
    private final String errorReason;

    public ErrorException(String message, String errorReason) {
        this.message = message;
        this.errorReason = errorReason;
    }

    public ErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message1, String errorReason) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message1;
        this.errorReason = errorReason;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getErrorReason() {
        return errorReason;
    }
}
