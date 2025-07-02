package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.Marker;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;


    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto add(@Valid @RequestBody RequestItemDto requestItemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.add(userId, requestItemDto);
    }

    @PatchMapping("/{itemsId}")
    public ItemDto update(@PathVariable long itemsId, @RequestBody RequestItemDto requestItemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemService.update(itemsId, requestItemDto, userId);
    }

    @GetMapping("/{itemsId}")
    public ItemDto getById(@PathVariable long itemsId) {
        return itemService.getById(itemsId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("{itemId}/comment")
    @Validated
    public CommentDto addComment(@Valid @PathVariable Long itemId, @RequestBody RequestCommentDto requestCommentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(itemId, requestCommentDto, userId);
    }
}