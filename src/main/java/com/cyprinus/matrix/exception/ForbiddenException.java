package com.cyprinus.matrix.exception;

public class ForbiddenException extends Exception {

    private Boolean fatal = false;

    public Boolean getFatal() {
        return fatal;
    }

    public ForbiddenException(String message, Boolean fatal) {
        super(message);
        this.fatal = fatal;
    }

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    protected ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
