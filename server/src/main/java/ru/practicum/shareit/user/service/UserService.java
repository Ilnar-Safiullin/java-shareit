package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;


public interface UserService {

    UserDto add(RequestUserDto requestUserDto);

    UserDto updateUser(long userId, RequestUserDto requestUserDto);

    void deleteUser(long userId);

    UserDto getUserById(long userId);
}
