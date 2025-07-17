package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto addItemRequest(RequestItemDto requestItemDto, Long userId);

    List<RequestDto> getUserItemRequests(Long userId);

    List<RequestDto> getOtherUsersItemRequests(Long userId);

    RequestDto getItemRequestById(Long requestId, Long userId);
}
