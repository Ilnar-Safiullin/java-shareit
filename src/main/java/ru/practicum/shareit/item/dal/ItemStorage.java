package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;


import java.util.List;

public interface ItemStorage {

    public Item add(long userId, Item newItem);

    public Item update(Item updateItem);

    public Item getById(long itemsId);

    public List<Item> getItemsByOwner(Long userId);

    public List<Item> search(String text);
}
