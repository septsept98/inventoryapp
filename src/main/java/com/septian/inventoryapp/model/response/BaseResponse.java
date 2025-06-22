package com.septian.inventoryapp.model.response;

public class BaseResponse<T> {
    private String message;
    private T content;

    public BaseResponse() {
    }

    public BaseResponse(String message, T content) {
        this.message = message;
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
