package io.github.divinator.exception;

public class CallLogServiceException extends Exception{

    public CallLogServiceException(String message) {
        super(message);
    }

    public CallLogServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
