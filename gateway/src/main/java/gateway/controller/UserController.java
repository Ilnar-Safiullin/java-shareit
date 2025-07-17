package gateway.controller;

import gateway.annotation.Marker;
import gateway.dto.RequestUserDto;
import gateway.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final RestTemplate restTemplate;
    private final String shareitServiceUrl = "http://localhost:9090/users";

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<UserDto> add(@Valid @RequestBody RequestUserDto requestUserDto) {
        log.info("Попытка добавить User");
        return sendRequest(shareitServiceUrl, HttpMethod.POST, requestUserDto, UserDto.class);
    }

    @PatchMapping("/{userId}")
    @Validated(Marker.OnUpdate.class)
    public ResponseEntity<UserDto> updateUser(@PathVariable long userId,
                                        @Valid @RequestBody RequestUserDto requestUserDto) {
        log.info("Попытка обновить User");
        String url = shareitServiceUrl + "/" + userId;
        return sendRequest(url, HttpMethod.PATCH, requestUserDto, UserDto.class);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        String url = shareitServiceUrl + "/" + userId;
        return sendRequest(url, HttpMethod.DELETE, null, Void.class);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long userId) {
        String url = shareitServiceUrl + "/" + userId;
        return sendRequest(url, HttpMethod.GET, null, UserDto.class);
    }

    private <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    private <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

}