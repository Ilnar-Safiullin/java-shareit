package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static User mapToUser(RequestUserDto requestUserDto) {
        User user = new User();
        user.setName(requestUserDto.getName());
        user.setEmail(requestUserDto.getEmail());
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static void updateUserFromRequest(User existingUser, RequestUserDto requestUserDto) {
        if (requestUserDto.getName() != null) {
            existingUser.setName(requestUserDto.getName());
        }
        if (requestUserDto.getEmail() != null) {
            existingUser.setEmail(requestUserDto.getEmail());
        }
    }
}
