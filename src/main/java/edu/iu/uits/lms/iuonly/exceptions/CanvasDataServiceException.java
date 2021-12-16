package edu.iu.uits.lms.iuonly.exceptions;

/**
 * Created by chmaurer on 5/20/16.
 */
public class CanvasDataServiceException extends Exception {

    public CanvasDataServiceException() {
        super();
    }

    public CanvasDataServiceException(Throwable cause) {
        super(cause);
    }

    public CanvasDataServiceException(String message) {
        super(message);
    }

    public CanvasDataServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    protected CanvasDataServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
