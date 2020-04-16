package com.cyprinus.matrix.exception;
public class UnauthorizedException extends MatrixBaseException {

    private Boolean fatal = false;

    public Boolean getFatal() {
        return fatal;
    }

    public UnauthorizedException() {
        super("没有授权！");
    }

    public UnauthorizedException(String message, Boolean fatal) {
        super(message);
        this.fatal = fatal;
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super("没有授权！", cause);
    }

    protected UnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
