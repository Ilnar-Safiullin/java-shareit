package gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
