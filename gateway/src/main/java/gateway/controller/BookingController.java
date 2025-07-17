package gateway.controller;

import gateway.dto.BookingDto;
import gateway.dto.RequestBookingDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final RestTemplate restTemplate;
    private final String shareitServiceUrl = "http://localhost:9090/bookings";


    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody RequestBookingDto requestBookingDto,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        String url = shareitServiceUrl;
        return sendRequest(url, HttpMethod.POST, requestBookingDto, BookingDto.class, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        String url = shareitServiceUrl + "/" + bookingId;
        return sendRequest(url, HttpMethod.GET, null, BookingDto.class, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@PathVariable Long bookingId,
                                                     @RequestParam Boolean approved,
                                                     @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        String url = shareitServiceUrl + "/" + bookingId + "?approved=" + approved;
        return sendRequest(url, HttpMethod.PATCH, null, BookingDto.class, ownerId);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(defaultValue = "ALL") State state) {
        String url = shareitServiceUrl + "?state=" + state;
        return sendRequest(url, HttpMethod.GET, null,new ParameterizedTypeReference<List<BookingDto>>() {}, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(defaultValue = "ALL") State state) {
        return sendRequest(shareitServiceUrl + "/owner?state=" + state, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BookingDto>>() {}, ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return sendRequest(shareitServiceUrl + "/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BookingDto>>() {}, null);
    }

    private <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, Class<T> responseType, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    private <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, ParameterizedTypeReference<T> responseType, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }
}