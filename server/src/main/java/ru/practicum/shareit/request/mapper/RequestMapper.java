package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;

public class RequestMapper {

    public static Request mapToRequest(RequestItemDto requestItemDto) {
        Request request = new Request();
        request.setDescription(requestItemDto.getDescription());
        request.setCreated(requestItemDto.getCreated());
        return request;
    }

    public static RequestDto mapToRequestDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setRequester(UserMapper.mapToUserDto(request.getRequester()));
        dto.setCreated(request.getCreated());
        return dto;
    }
}
