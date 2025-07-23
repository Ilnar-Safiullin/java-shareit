package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dal.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;


    @Override
    public UserDto add(RequestUserDto requestUserDto) {
        User user = UserMapper.mapToUser(requestUserDto);
        user = userStorage.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, RequestUserDto requestUserDto) {
        Optional<User> userOptional = userStorage.findById(userId);
        User existingUser = userOptional.orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        UserMapper.updateUserFromRequest(existingUser, requestUserDto);
        existingUser = userStorage.save(existingUser);
        return UserMapper.mapToUserDto(existingUser);
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        userStorage.deleteById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        Optional<User> userOptional = userStorage.findById(userId);
        User existingUser = userOptional.orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        return UserMapper.mapToUserDto(existingUser);
    }
}
