package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemBodyDto;

import java.util.List;

public interface ItemService {

    ItemDto add(Long userId, ItemBodyDto itemBodyDto);

    ItemDto update(Long itemsId, ItemBodyDto itemBodyDto, Long userId);

    ItemDto getById(Long itemsId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, RequestCommentDto requestCommentDto, Long userId);
}
