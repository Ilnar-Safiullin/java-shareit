package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;


public interface UserService {

    public UserDto add(UserDto userDto);

    public UserDto updateUser(long userId, UserDto userDto);

    public void deleteUser(long userId);

    public UserDto getUserById(long userId);
}
