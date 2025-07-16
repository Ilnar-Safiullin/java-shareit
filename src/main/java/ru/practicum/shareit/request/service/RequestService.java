package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<RequestDto> getUserItemRequests(Long userId);

    List<RequestDto> getOtherUsersItemRequests(Long userId);

    RequestDto getItemRequestById(Long requestId, Long userId);
}
