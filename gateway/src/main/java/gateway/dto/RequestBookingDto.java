package gateway.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestBookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
