package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(Long userId, RequestItemDto requestItemDto);

    ItemDto update(Long itemsId, RequestItemDto requestItemDto, Long userId);

    ItemDto getById(Long itemsId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, RequestCommentDto requestCommentDto, Long userId);
}
