package com.septian.inventoryapp.model.dto;

import org.springframework.http.HttpStatus;

public class ErrorException extends RuntimeException {

    private final String message;
    private final String errorReason;
    private final HttpStatus status;

    public ErrorException(String message, String errorReason, HttpStatus status) {
        this.message = message;
        this.errorReason = errorReason;
        this.status = status;
    }

    public ErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message1, String errorReason, HttpStatus status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message1;
        this.errorReason = errorReason;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
