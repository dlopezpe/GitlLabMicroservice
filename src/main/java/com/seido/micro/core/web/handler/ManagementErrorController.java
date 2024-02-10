package com.seido.micro.core.web.handler;

import com.seido.micro.core.utils.exception.ValidationException;
import com.seido.micro.core.utils.resource.ErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Spring Controler advice to manage the excetion throws by the application
 */
@ControllerAdvice
public class ManagementErrorController {


    /**
     * Handler to manage validation Exceptions
     * @param ex ValidationException
     * @return ResponseEntity<ErrorResource>
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResource> validationError(ValidationException ex) {
        ErrorResource error = new ErrorResource();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setException(ex.getCause().getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
