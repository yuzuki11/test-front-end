package com.cyprinus.matrix.exception;

public class ForbiddenException extends MatrixBaseException {

    private Boolean fatal = false;

    public Boolean getFatal() {
        return fatal;
    }

    public ForbiddenException() {
        super("没有操作权限！");
    }

    public ForbiddenException(String message, Boolean fatal) {
        super(message);
        this.fatal = fatal;
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super("没有操作权限！", cause);
    }

    protected ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
