package io.github.divinator.javafx;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * FxLoadException wraps {@link IOException}s thrown during the processing with {@link FXMLLoader}.
 * It is a RuntimeException and helps with debugging the actual FXML location used.
 *
 * @author Rene Gielen
 */
public class FXMLLoadException extends RuntimeException {

    public FXMLLoadException() {
        super();
    }

    public FXMLLoadException(String message) {
        super(message);
    }

    public FXMLLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FXMLLoadException(Throwable cause) {
        super(cause);
    }

    protected FXMLLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
