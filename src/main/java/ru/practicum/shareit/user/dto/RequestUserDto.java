package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.annotation.Marker;

@Data
public class RequestUserDto {
    private String name;

    @Email(message = "Ошибка в email", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(message = "Email обязателен", groups = Marker.OnCreate.class)
    private String email;
}
