package com.cyprinus.matrix.exception;

public class MatrixBaseException extends Exception {

    public MatrixBaseException() {
        super();
    }

    MatrixBaseException(String message) {
        super(message);
    }

    MatrixBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixBaseException(Throwable cause) {
        super(cause);
    }

    MatrixBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
