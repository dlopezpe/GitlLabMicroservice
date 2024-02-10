package com.seido.micro.core.utils.resource;

/**
 * Transfer class to pass to HttpEntity at the exception Handler
 */
public class ErrorResource {

    private Integer status;
    private String error;
    private String exception;
    private String message;
    private String parameters;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParameters() { return parameters;   }

    public void setParameters(String parameters) {     this.parameters = parameters;  }
}

