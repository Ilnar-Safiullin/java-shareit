package gateway.controller;

import gateway.annotation.Marker;
import gateway.client.ItemRequestClient;
import gateway.dto.RequestItemDto;
import gateway.dto.RequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<RequestDto> addItemRequest(@Valid @RequestBody RequestItemDto requestItemDto,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GATEWAY Попытка добавить Request");
        return itemRequestClient.addItemRequest(requestItemDto, userId);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getOtherUsersItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long requestId) {
        log.info("GATEWAY Попытка получить Request по id");
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}