package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private Instant timestamp;
    private Map<String, String> details;

    public ErrorResponse(String error, String message) {
        this(error, message, Instant.now(), null);
    }

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message);
    }
}