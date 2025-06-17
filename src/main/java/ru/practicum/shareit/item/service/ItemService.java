package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    public ItemDto add(Long userId, ItemDto itemDto);

    public ItemDto update(Long itemsId, ItemDto itemDto, Long userId);

    public ItemDto getById(Long itemsId);

    public List<ItemDto> getItemsByOwner(Long userId);

    public List<ItemDto> search(String text);
}
