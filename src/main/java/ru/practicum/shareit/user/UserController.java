package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.Marker;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserServiceImpl userServiceImpl;


    @PostMapping
    @Validated(Marker.OnCreate.class)
    public UserDto add(@Valid @RequestBody RequestUserDto requestUserDto) {
        return userServiceImpl.add(requestUserDto);
    }

    @PatchMapping("/{userId}")
    @Validated(Marker.OnUpdate.class)
    public UserDto updateUser(@PathVariable long userId, @RequestBody RequestUserDto requestUserDto) {
        return userServiceImpl.updateUser(userId, requestUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        try {
            userServiceImpl.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении" + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userServiceImpl.getUserById(userId);
    }
}