package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingTimeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dal.CommentStorage;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;


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
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        List<CommentDto> commentDtos = commentStorage.findAllByItemId(itemsId).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
        itemDto.setComments(commentDtos);
        return itemDto;

    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) { //Очень сложный метод можно ли его упростить?
        List<Item> items = itemStorage.findByOwnerId(userId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        // Получаем комментарии
        List<Comment> comments = commentStorage.findAllByItemIds(itemIds);
        Map<Long, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        // Получаем последние и следующие бронирования
        Map<Long, BookingDto> lastBookings = new HashMap<>();
        Map<Long, BookingDto> nextBookings = new HashMap<>();

        for (Long itemId : itemIds) {
            Booking lastBooking = bookingStorage.findLastBooking(itemId);
            if (lastBooking != null) {
                lastBookings.put(itemId, BookingMapper.mapToBookingDto(lastBooking));
            }

            Booking nextBooking = bookingStorage.findNextBooking(itemId);
            if (nextBooking != null) {
                nextBookings.put(itemId, BookingMapper.mapToBookingDto(nextBooking));
            }
        }

        // Преобразуем в ItemDto
        return items.stream().map(item -> {
            ItemDto itemDto = ItemMapper.mapToItemDto(item);
            itemDto.setComments(commentsByItem.getOrDefault(itemDto.getId(), List.of()).stream()
                    .map(CommentMapper::mapToCommentDto)
                    .toList());

            itemDto.setLastBooking(lastBookings.get(itemDto.getId()));
            itemDto.setNextBooking(nextBookings.get(itemDto.getId()));

            return itemDto;
        }).toList();
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

    @Override
    public CommentDto addComment(Long itemId, RequestCommentDto requestCommentDto, Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found id: " + userId));
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found id: " + itemId));
        Booking booking = bookingStorage.findByItemIdAndUserId(itemId, userId).orElseThrow(() -> new NotFoundException("Booking not found"));
        LocalDateTime start = booking.getStart(); //пришлось добавить иначе просто запросом с БД он не проходил постман тест
        LocalDateTime end = booking.getEnd(); //пришлось добавить иначе просто запросом с БД он не проходил постман тест
        LocalDateTime now = LocalDateTime.now(); //пришлось добавить иначе просто запросом с БД он не проходил постман тест
        if (!commentStorage.existsApprovedPastBookingForUserAndItem(itemId, userId)) {
            throw new BookingTimeException("Нет законченных бронирований");
        }
        if (start.isAfter(now) || end.isAfter(now)) { //пришлось добавить иначе просто запросом с БД он не проходил постман тест
            throw new BookingTimeException("Нет законченных бронирований");
        }
        Comment comment = CommentMapper.mapToComment(requestCommentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment = commentStorage.save(comment);
        return CommentMapper.mapToCommentDto(comment);
    }
}