package com.cyprinus.matrix.exception;

public class NotFoundException extends MatrixBaseException {

    private Boolean fatal = false;

    public Boolean getFatal() {
        return fatal;
    }

    public NotFoundException() {
        super("没有找到资源！");
    }

    public NotFoundException(String message, Boolean fatal) {
        super(message);
        this.fatal = fatal;
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super("没有找到资源！", cause);
    }

    protected NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
