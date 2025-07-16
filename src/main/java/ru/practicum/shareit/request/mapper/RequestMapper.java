package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;

public class RequestMapper {

    public static Request mapToRequest(ItemRequestDto itemRequestDto) {
        Request request = new Request();
        request.setDescription(itemRequestDto.getDescription());
        request.setCreated(itemRequestDto.getCreated());
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
