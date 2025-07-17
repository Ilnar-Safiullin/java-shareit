package gateway.controller;

import gateway.annotation.Marker;
import gateway.dto.CommentDto;
import gateway.dto.ItemDto;
import gateway.dto.RequestCommentDto;
import gateway.dto.RequestItemDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final RestTemplate restTemplate;
    private final String shareitServiceUrl = "http://localhost:9090/items";


    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<ItemDto> add(@Valid @RequestBody RequestItemDto requestItemDto,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        String url = shareitServiceUrl;
        return sendRequest(url, HttpMethod.POST, requestItemDto, ItemDto.class, userId);
    }

    @PatchMapping("/{itemsId}")
    public ResponseEntity<ItemDto> update(@PathVariable long itemsId,
                                          @Valid @RequestBody RequestItemDto requestItemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        String url = shareitServiceUrl + "/" + itemsId;
        return sendRequest(url, HttpMethod.PATCH, requestItemDto, ItemDto.class, userId);
    }

    @GetMapping("/{itemsId}")
    public ResponseEntity<ItemDto> getById(@PathVariable long itemsId) {
        String url = shareitServiceUrl + "/" + itemsId;
        return sendRequest(url, HttpMethod.GET, null, ItemDto.class, null);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        String url = shareitServiceUrl + "?ownerId=" + userId;
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<ItemDto>>() {}, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        String url = shareitServiceUrl + "/search?text=" + text;
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemDto>>() {}, userId);
    }

    @PostMapping("{itemId}/comment")
    @Validated
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId,
                                                 @Valid @RequestBody RequestCommentDto requestCommentDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        String url = shareitServiceUrl + "/" + itemId + "/comment";
        return sendRequest(url, HttpMethod.POST, requestCommentDto, CommentDto.class, userId);
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