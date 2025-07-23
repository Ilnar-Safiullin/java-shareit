package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        String errorMessage = "Validation error message";
        ValidationException ex = new ValidationException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequestWithDetails() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "default message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentNotValid(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FieldValidation", response.getBody().getError());
        assertEquals("Ошибка валидации полей", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getDetails());
        assertEquals(1, response.getBody().getDetails().size());
        assertTrue(response.getBody().getDetails().containsKey("field"));
        assertEquals("default message", response.getBody().getDetails().get("field"));
    }

    @Test
    void handleBookingTimeException_ShouldReturnBadRequest() {
        String errorMessage = "Booking time error";
        BookingTimeException ex = new BookingTimeException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBookingTimeException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BookingError", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        String errorMessage = "Resource not found";
        NotFoundException ex = new NotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NotFound", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("General error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGeneralException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("InternalError", response.getBody().getError());
        assertEquals("Внутренняя ошибка сервера", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void errorResponseOf_ShouldCreateResponseWithErrorAndMessage() {
        String error = "TestError";
        String message = "Test message";

        ErrorResponse response = ErrorResponse.of(error, message);

        assertNotNull(response);
        assertEquals(error, response.getError());
        assertEquals(message, response.getMessage());
        assertNotNull(response.getTimestamp());
        assertNull(response.getDetails());
    }

    @Test
    void errorResponseConstructor_ShouldCreateFullResponse() {
        String error = "TestError";
        String message = "Test message";
        Instant timestamp = Instant.now();
        Map<String, String> details = Collections.singletonMap("key", "value");

        ErrorResponse response = new ErrorResponse(error, message, timestamp, details);

        assertNotNull(response);
        assertEquals(error, response.getError());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals(details, response.getDetails());
    }
}