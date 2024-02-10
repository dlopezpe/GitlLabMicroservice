package com.seido.micro.core.utils.exception;


/**
 * Generic exception to controlthe exceptions that come from the services
 */
public class ValidationException extends Exception {

    /**
     * Create excpetion from message
     * @param message String
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     *  Create exception from message and a Throwable
     * @param message String
     * @param e Throwable
     */
    public ValidationException(String message, Throwable e) {
        super(message, e);
    }

}
