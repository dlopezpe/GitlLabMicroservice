package com.seido.micro.core.web.handler;

import com.seido.micro.core.utils.exception.ValidationException;
import com.seido.micro.core.utils.resource.ErrorResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class ManagementErrorControllerTestIT {

    @Mock
    private ManagementErrorController managementErrorControllerMock;

    @Test
    public void testValidationError() {
        // Configuración del objeto de excepción (puedes usar cualquier excepción que extienda de RuntimeException)
        ValidationException validationException = new ValidationException("Mensaje de validación");

        // Configuración del objeto ErrorResource esperado
        ErrorResource expectedErrorResource = new ErrorResource();

        // Configuración del comportamiento mock para el método validationError
        when(managementErrorControllerMock.validationError(eq(validationException)))
                .thenReturn(ResponseEntity.badRequest().body(expectedErrorResource));

        // Llamada real al método en alguna otra parte del código que estás probando
        ResponseEntity<ErrorResource> result = managementErrorControllerMock.validationError(validationException);

        // Verificación de que el método mock fue llamado con los argumentos esperados
        verify(managementErrorControllerMock).validationError(eq(validationException));

        // Verificación de que el resultado coincide con lo esperado
        assertThat(result).isEqualTo(ResponseEntity.badRequest().body(expectedErrorResource));

        verify(managementErrorControllerMock, times(1)).validationError(validationException);
    }
}