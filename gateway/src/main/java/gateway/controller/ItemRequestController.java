package gateway.controller;

import gateway.dto.ItemRequestDto;
import gateway.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RestTemplate restTemplate;
    private final String shareitServiceUrl = "http://localhost:9090/requests";


    @PostMapping
    public ResponseEntity<RequestDto> addItemRequest(@RequestBody ItemRequestDto itemRequestCreate,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return sendRequest(shareitServiceUrl, HttpMethod.POST, itemRequestCreate, RequestDto.class, userId);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return sendRequest(shareitServiceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        String url = shareitServiceUrl + "/all";
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        String url = shareitServiceUrl + "/" + requestId;
        return sendRequest(url, HttpMethod.POST, null, RequestDto.class, userId);
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