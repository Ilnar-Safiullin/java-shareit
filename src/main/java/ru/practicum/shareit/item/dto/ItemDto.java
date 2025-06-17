package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.annotation.Marker;

import java.util.HashMap;


@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым", groups = {Marker.OnCreate.class})
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = {Marker.OnCreate.class})
    private String description;

    @NotNull(message = "Cтатус не может быть пустым", groups = {Marker.OnCreate.class})
    private Boolean available;

    private int rentCount;

    private HashMap<Long, String> reviews = new HashMap<>();

}
