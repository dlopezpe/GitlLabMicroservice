package com.santander.supra.core.web.handler;

import com.santander.supra.core.utils.exception.*;
import com.santander.supra.core.utils.resource.ErrorResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class ManagementErrorControllerTestIT {



    @Test
    public void validationError_ValidationException() {

        ManagementErrorController managementErrorController = new ManagementErrorController();
        ValidationException e = new ValidationException("TEST");
        ResponseEntity<ErrorResource> resp = managementErrorController.validationError(e);
        assertEquals(ValidationException.class.getName(), resp.getBody().getException());
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }



}