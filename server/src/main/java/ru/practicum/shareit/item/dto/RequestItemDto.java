package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.annotation.Marker;

@Data
public class RequestItemDto {

    @NotBlank(message = "Имя не может быть пустым", groups = {Marker.OnCreate.class})
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = {Marker.OnCreate.class})
    private String description;

    @NotNull(message = "Cтатус не может быть пустым", groups = {Marker.OnCreate.class})
    private Boolean available;
}