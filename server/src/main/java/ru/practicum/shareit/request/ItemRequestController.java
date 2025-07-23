package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestService requestService;

    @PostMapping
    public RequestDto addItemRequest(@RequestBody RequestItemDto requestItemDto,
                                     @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("SERVER Попытка добавить Request");
        return requestService.addItemRequest(requestItemDto, userId);
    }

    @GetMapping
    public List<RequestDto> getUserItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getOtherUsersItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getOtherUsersItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getItemRequestById(@PathVariable Long requestId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("SERVER Попытка получить Request по id");
        return requestService.getItemRequestById(requestId, userId);
    }
}