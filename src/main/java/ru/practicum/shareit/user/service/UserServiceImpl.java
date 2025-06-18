package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dal.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;


    @Override
    public UserDto add(RequestUserDto requestUserDto) {
        User user = UserMapper.mapToUser(requestUserDto);
        user = userStorage.add(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, RequestUserDto requestUserDto) {
        User existingUser = userStorage.getUserById(userId);
        UserMapper.updateUserFromRequest(existingUser, requestUserDto);
        existingUser = userStorage.updateUser(existingUser);
        return UserMapper.mapToUserDto(existingUser);
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.getUserById(userId);
        userStorage.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userStorage.getUserById(userId);
        return UserMapper.mapToUserDto(user);
    }
}
