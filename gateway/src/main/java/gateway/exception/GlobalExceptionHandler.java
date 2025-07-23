package gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.FieldError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "Некорректное значение"
                ));

        log.warn("Ошибки валидации в Gateway: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "ValidationError",
                        "Ошибка валидации входных данных",
                        errors
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        log.warn("Ошибки валидации параметров в Gateway: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "ParameterValidationError",
                        "Ошибка валидации параметров запроса",
                        errors
                ));
    }

    //Обработка ошибок от сервера
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(HttpClientErrorException ex) {
        try {
            ErrorResponse serverError = objectMapper.readValue(
                    ex.getResponseBodyAsString(),
                    ErrorResponse.class
            );

            log.warn("Получена ошибка от сервера ({}): {}", ex.getStatusCode(), serverError);
            return ResponseEntity.status(ex.getStatusCode())
                    .body(serverError);

        } catch (IOException e) {
            log.error("Ошибка парсинга ответа сервера: {}", ex.getResponseBodyAsString(), e);
            return ResponseEntity.status(ex.getStatusCode())
                    .body(new ErrorResponse(
                            "ServerResponseError",
                            "Не удалось обработать ответ сервера: " + ex.getStatusText()
                    ));
        }
    }

    // Обработка ошибок соединения с сервером
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException ex) {
        log.error("Ошибка соединения с сервером: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(
                        "ServiceUnavailable",
                        "Сервер временно недоступен"
                ));
    }

    // Общая обработка всех остальных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Необработанная ошибка в Gateway: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "InternalGatewayError",
                        "Внутренняя ошибка Gateway"
                ));
    }
}