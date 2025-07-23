package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void add_shouldSaveAndReturnUserDto() {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@example.com");
        User savedUser = new User(1L, "testName", "test@example.com");

        when(userStorage.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.add(requestUserDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        long userId = 1L;
        RequestUserDto requestUserDto = new RequestUserDto("Updated Name", "updated@example.com");
        User existingUser = new User(userId, "testName", "test@example.com");
        User updatedUser = new User(userId, "Updated Name", "updated@example.com");

        when(userStorage.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, requestUserDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(requestUserDto.getName(), result.getName());
        assertEquals(requestUserDto.getEmail(), result.getEmail());

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowNotFoundExceptionWhenUserNotFound() {
        long userId = 99L;
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@example.com");

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(userId, requestUserDto);
        });

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, never()).save(any());
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        long userId = 1L;
        User existingUser = new User(userId, "testName", "test@example.com");

        when(userStorage.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userStorage).deleteById(userId);

        userService.deleteUser(userId);

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowNotFoundExceptionWhenUserNotFound() {
        long userId = 99L;

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, never()).deleteById(any());
    }

    @Test
    void getUserById_shouldReturnUserDtoWhenUserExists() {
        long userId = 1L;
        User existingUser = new User(userId, "testName", "test@example.com");

        when(userStorage.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(existingUser.getName(), result.getName());
        assertEquals(existingUser.getEmail(), result.getEmail());

        verify(userStorage, times(1)).findById(userId);
    }

    @Test
    void getUserById_shouldThrowNotFoundExceptionWhenUserNotFound() {
        long userId = 99L;

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        verify(userStorage, times(1)).findById(userId);
    }
}
