package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemBodyDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;


    @PostMapping
    public ItemDto add(@RequestBody ItemBodyDto itemBodyDto,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.add(userId, itemBodyDto);
    }

    @PatchMapping("/{itemsId}")
    public ItemDto update(@PathVariable long itemsId, @RequestBody ItemBodyDto itemBodyDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemService.update(itemsId, itemBodyDto, userId);
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
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody RequestCommentDto requestCommentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(itemId, requestCommentDto, userId);
    }
}