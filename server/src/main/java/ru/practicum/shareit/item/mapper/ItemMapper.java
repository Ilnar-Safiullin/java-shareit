package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemBodyDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.RequestMapper;

public class ItemMapper {


    public static Item mapToItem(ItemBodyDto itemBodyDto) {
        Item item = new Item();
        item.setName(itemBodyDto.getName());
        item.setDescription(itemBodyDto.getDescription());
        item.setAvailable(itemBodyDto.getAvailable());
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            dto.setRequest(RequestMapper.mapToRequestDto(item.getRequest()));
        }
        return dto;
    }

    public static void updateItemFromRequest(Item existingItem, ItemBodyDto itemBodyDto) {
        if (itemBodyDto.getName() != null) {
            existingItem.setName(itemBodyDto.getName());
        }
        if (itemBodyDto.getDescription() != null) {
            existingItem.setDescription(itemBodyDto.getDescription());
        }
        if (itemBodyDto.getAvailable() != null) {
            existingItem.setAvailable(itemBodyDto.getAvailable());
        }
    }
}