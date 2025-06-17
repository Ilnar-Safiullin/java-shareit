package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemMemoryStorage implements ItemStorage{
    private final Map<Long, Item> items = new HashMap<>();
    private Long counterId = 0L;


    @Override
    public Item add(long userId, Item newItem) {
        Item item = new Item();
        item.setId(++counterId);
        item.setName(newItem.getName());
        item.setOwner(userId);
        item.setDescription(newItem.getDescription());
        item.setAvailable(newItem.getAvailable());
        item.setRentCount(newItem.getRentCount());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item updateItem) {
        return items.put(updateItem.getId(), updateItem);
    }

    @Override
    public Item getById(long itemsId) {
        Item item = items.get(itemsId);
        if (item == null) {
            throw new NotFoundException("Item с таким айди не найден");
        }
        return item;
    }

    @Override
    public List<Item> getItemsByOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable() == true)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }
}
