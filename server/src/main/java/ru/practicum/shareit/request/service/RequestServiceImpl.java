package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.RequestStorage;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public RequestDto addItemRequest(RequestItemDto requestItemDto, Long userId) {
        Request request = RequestMapper.mapToRequest(requestItemDto);
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found id: " + userId));
        request.setRequester(user);
        request = requestStorage.save(request);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public List<RequestDto> getUserItemRequests(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found id: " + userId));
        List<Request> requests = requestStorage.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    public List<RequestDto> getOtherUsersItemRequests(Long requesterId) {
        userStorage.findById(requesterId).orElseThrow(() -> new NotFoundException("User not found id: " + requesterId));
        List<Request> requests = requestStorage.findByRequesterIdNotOrderByCreatedDesc(requesterId);
        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    public RequestDto getItemRequestById(Long requestId, Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found id: " + userId));
        Request request = requestStorage.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));
        List<Item> items = itemStorage.findByRequestId(requestId);
        Set<ItemDto> itemsDto = items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toSet());
        RequestDto requestDto = RequestMapper.mapToRequestDto(request);
        requestDto.setItems(itemsDto);
        return requestDto;
    }
}
