package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.user.dal.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    @Override
    public ItemDto add(Long userId, RequestItemDto requestItemDto) {
        userStorage.getUserById(userId);
        Item item = ItemMapper.mapToItem(requestItemDto);
        item = itemStorage.add(userId, item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(Long itemsId, RequestItemDto requestItemDto, Long userId) {
        Item existingItem = itemStorage.getById(itemsId);
        if (!existingItem.getOwner().equals(userId)) {
            throw new NotFoundException("Вы не можете редактировать чужую вещь");
        }
        ItemMapper.updateItemFromRequest(existingItem, requestItemDto);
        itemStorage.update(existingItem);
        return ItemMapper.mapToItemDto(existingItem);
    }

    @Override
    public ItemDto getById(Long itemsId) {
        Item item = itemStorage.getById(itemsId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        userStorage.getUserById(userId);
        return itemStorage.getItemsByOwner(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
