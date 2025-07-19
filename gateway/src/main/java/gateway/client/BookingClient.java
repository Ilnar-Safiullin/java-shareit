package gateway.client;

import gateway.controller.State;
import gateway.dto.BookingDto;
import gateway.dto.RequestBookingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class BookingClient extends BaseClient {
    private final String serverStringUrl = "http://localhost:9090/bookings"; //не могу сделать заглавными ругается чекСтайл. И статик нельзя наверное, он же во всех классах есть


    protected BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<BookingDto> createBooking(RequestBookingDto requestBookingDto, Long userId) {
        return sendRequest(serverStringUrl, HttpMethod.POST, requestBookingDto, BookingDto.class, userId);
    }

    public ResponseEntity<BookingDto> getBookingById(Long bookingId, Long userId) {
        String url = serverStringUrl + "/" + bookingId;
        return sendRequest(url, HttpMethod.GET, null, BookingDto.class, userId);
    }

    public ResponseEntity<BookingDto> approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        String url = serverStringUrl + "/" + bookingId + "?approved=" + approved;
        return sendRequest(url, HttpMethod.PATCH, null, BookingDto.class, ownerId);
    }

    public ResponseEntity<List<BookingDto>> getUserBookings(Long userId, State state) {
        String url = serverStringUrl + "?state=" + state;
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BookingDto>>() {}, userId);
    }

    public ResponseEntity<List<BookingDto>> getOwnerBookings(Long ownerId, State state) {
        return sendRequest(serverStringUrl + "/owner?state=" + state, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BookingDto>>() {}, ownerId);
    }

    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return sendRequest(serverStringUrl + "/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BookingDto>>() {}, null);
    }
}
