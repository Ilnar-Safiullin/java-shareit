package gateway.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleRestClientException_WhenHttpClientErrorException_ShouldReturnCorrectResponse() {
        String errorMessage = "User not found";
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.NOT_FOUND,
                "Not Found",
                errorMessage.getBytes(),
                null
        );

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRestClientException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleRestClientException_WhenGenericRestClientException_ShouldReturnInternalServerError() {
        RestClientException exception = new RestClientException("Connection refused");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRestClientException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Произошла ошибка при обращении к серверу", response.getBody().getMessage());
    }

    @Test
    void handleRestClientException_WhenHttpClientErrorExceptionWithEmptyBody_ShouldHandleGracefully() {
        HttpClientErrorException exception = mock(HttpClientErrorException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(exception.getResponseBodyAsString()).thenReturn(null);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRestClientException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    @Test
    void handleRestClientException_WhenHttpClientErrorExceptionWithComplexBody_ShouldReturnRawBody() {
        String jsonBody = "{\"error\":\"Invalid input\",\"details\":[\"Email is invalid\"]}";
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                jsonBody.getBytes(),
                null
        );

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRestClientException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(jsonBody, response.getBody().getMessage());
    }

    @Test
    void handleRestClientException_WhenResponseStatusException_ShouldReturnCorrectStatus() {
        String errorMessage = "Service unavailable";
        RestClientException exception = new RestClientException(errorMessage) {
        };

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRestClientException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Произошла ошибка при обращении к серверу", response.getBody().getMessage());
    }
}