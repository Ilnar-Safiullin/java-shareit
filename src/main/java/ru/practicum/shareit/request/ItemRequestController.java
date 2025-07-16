package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestService requestService;

    @PostMapping
    public RequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestCreate,
                                     @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.addItemRequest(itemRequestCreate, userId);
    }

    @GetMapping
    public List<RequestDto> getUserItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getOtherUsersItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getOtherUsersItemRequests(userId);
    }

    @GetMapping("{requestId}")
    public RequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getItemRequestById(requestId, userId);
    }
}