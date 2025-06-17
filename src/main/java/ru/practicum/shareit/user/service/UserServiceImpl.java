package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dal.UserStorage;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserStorage userStorage;


    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user = userStorage.add(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User existingUser = userStorage.getUserById(userId);
        UserMapper.updateUserFromRequest(existingUser, userDto);
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
