package com.cyprinus.matrix.exception;

public class BadRequestException extends Exception {

    private Boolean fatal = false;

    public Boolean getFatal() {
        return fatal;
    }

    public BadRequestException() {
        super("不合理的请求！");
    }

    public BadRequestException(String message, Boolean fatal) {
        super(message);
        this.fatal = fatal;
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super("不合理的请求！", cause);
    }

    protected BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
