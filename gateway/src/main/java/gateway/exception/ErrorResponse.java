package gateway.exception;

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
    private Map<String, String> details;
    private Instant timestamp = Instant.now();

    public ErrorResponse(String error, String message) {
        this(error, message, null);
    }

    public ErrorResponse(String error, String message, Map<String, String> details) {
        this.error = error;
        this.message = message;
        this.details = details;
    }
}