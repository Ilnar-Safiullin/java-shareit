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
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    @Override
    public ItemDto add(Long userId, RequestItemDto requestItemDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found id: " + userId));
        Item item = ItemMapper.mapToItem(requestItemDto);
        item.setOwner(user);
        item = itemStorage.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(Long itemsId, RequestItemDto requestItemDto, Long userId) {
        Item existingItem = itemStorage.findById(itemsId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вы не можете редактировать чужую вещь");
        }
        ItemMapper.updateItemFromRequest(existingItem, requestItemDto);
        itemStorage.save(existingItem);
        return ItemMapper.mapToItemDto(existingItem);
    }

    @Override
    public ItemDto getById(Long itemsId) {
        Item item = itemStorage.findById(itemsId).orElseThrow(() -> new NotFoundException("Item not found id: " + itemsId));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found id: " + userId));
        return itemStorage.findByOwnerId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
