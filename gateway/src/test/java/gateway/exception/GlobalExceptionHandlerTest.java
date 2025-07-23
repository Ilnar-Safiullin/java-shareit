package gateway.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;


import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ValidationError", response.getBody().getError());
        assertTrue(response.getBody().getDetails().isEmpty());
    }

    @Test
    void handleValidationExceptions_WithFieldErrors_ShouldReturnErrorDetails() {
        FieldError fieldError1 = new FieldError("object", "email", "Invalid email");
        FieldError fieldError2 = new FieldError("object", "name", "Name required");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertEquals(2, response.getBody().getDetails().size());
        assertEquals("Invalid email", response.getBody().getDetails().get("email"));
    }

    @Test
    void handleValidationExceptions_WhenFieldErrorHasNullMessage_ShouldUseDefault() {
        FieldError fieldError = new FieldError("object", "field", null, false, null, null, null);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertEquals("Некорректное значение", response.getBody().getDetails().get("field"));
    }

    @Test
    void handleConstraintViolation_ShouldReturnBadRequestWithErrors() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("param");
        when(violation.getMessage()).thenReturn("Invalid value");

        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("Invalid value", response.getBody().getDetails().get("param"));
    }

    @Test
    void handleConstraintViolation_WhenNoViolations_ShouldReturnEmptyDetails() {
        ConstraintViolationException ex = new ConstraintViolationException(Collections.emptySet());

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(ex);

        assertTrue(response.getBody().getDetails().isEmpty());
    }

    @Test
    void handleHttpClientError_WithValidResponse_ShouldReturnServerError() throws Exception {
        String json = "{\"error\":\"NotFound\",\"message\":\"Not found\"}";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.NOT_FOUND,
                "Not Found",
                json.getBytes(),
                null
        );

        when(objectMapper.readValue(json, ErrorResponse.class))
                .thenReturn(new ErrorResponse("NotFound", "Not found"));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpClientError(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("NotFound", response.getBody().getError());
    }

    @Test
    void handleHttpClientError_WithParseError_ShouldReturnServerResponseError() throws Exception {
        String invalidJson = "invalid";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                invalidJson.getBytes(),
                null
        );

        when(objectMapper.readValue(invalidJson, ErrorResponse.class))
                .thenThrow(new JsonParseException(null, "Parse error"));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpClientError(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ServerResponseError", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Не удалось обработать ответ сервера"));
    }

    @Test
    void handleResourceAccessException_ShouldReturnServiceUnavailable() {
        ResourceAccessException ex = new ResourceAccessException("Connection failed");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceAccessException(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("ServiceUnavailable", response.getBody().getError());
    }

    @Test
    void handleAllExceptions_ShouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAllExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("InternalGatewayError", response.getBody().getError());
    }

    @Test
    void errorResponse_ShouldContainTimestamp() {
        ErrorResponse response = new ErrorResponse("Test", "Message");
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
    }
}