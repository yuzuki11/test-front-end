package com.cyprinus.matrix.exception;

public class ServerInternalException extends Exception {

    private Boolean fatal = false;

    public Boolean getFatal() {
        return fatal;
    }

    public ServerInternalException() {
        super("服务器内部错误！");
    }

    public ServerInternalException(String message, Boolean fatal) {
        super(message);
        this.fatal = fatal;
    }

    public ServerInternalException(String message) {
        super(message);
    }

    public ServerInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerInternalException(Throwable cause) {
        super("服务器内部错误！", cause);
    }

    protected ServerInternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
