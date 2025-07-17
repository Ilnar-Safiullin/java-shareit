package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dal.RequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    @Override
    public RequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        Request request = RequestMapper.mapToRequest(itemRequestDto);
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
        return RequestMapper.mapToRequestDto(request);
    }
}
