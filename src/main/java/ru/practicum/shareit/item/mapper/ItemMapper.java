package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {


    public static Item mapToItem(RequestItemDto requestItemDto) {
        Item item = new Item();
        item.setName(requestItemDto.getName());
        item.setDescription(requestItemDto.getDescription());
        item.setAvailable(requestItemDto.getAvailable());
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static void updateItemFromRequest(Item existingItem, RequestItemDto requestItemDto) {
        if (requestItemDto.getName() != null) {
            existingItem.setName(requestItemDto.getName());
        }
        if (requestItemDto.getDescription() != null) {
            existingItem.setDescription(requestItemDto.getDescription());
        }
        if (requestItemDto.getAvailable() != null) {
            existingItem.setAvailable(requestItemDto.getAvailable());
        }
    }

}
