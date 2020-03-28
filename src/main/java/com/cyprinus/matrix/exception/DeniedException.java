package com.cyprinus.matrix.exception;

public class DeniedException extends Exception {
    public DeniedException() {
        super();
    }

    public DeniedException(String message) {
        super(message);
    }

    public DeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeniedException(Throwable cause) {
        super(cause);
    }

    protected DeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
