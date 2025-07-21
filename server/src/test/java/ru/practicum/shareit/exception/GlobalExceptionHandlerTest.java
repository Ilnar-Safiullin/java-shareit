package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        String errorMessage = "Validation failed";
        ValidationException exception = new ValidationException(errorMessage);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleBookingTimeException_ShouldReturnBadRequest() {
        String errorMessage = "Booking time conflict";
        BookingTimeException exception = new BookingTimeException(errorMessage);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleBookingTimeException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleValidationException_WithEmptyMessage_ShouldReturnBadRequest() {
        ValidationException exception = new ValidationException("");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody().getMessage());
    }

    @Test
    void handleBookingTimeException_WithNullMessage_ShouldReturnBadRequest() {
        BookingTimeException exception = new BookingTimeException(null);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleBookingTimeException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }
}