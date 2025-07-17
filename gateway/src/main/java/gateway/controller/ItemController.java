package gateway.controller;

import gateway.annotation.Marker;
import gateway.client.ItemClient;
import gateway.dto.CommentDto;
import gateway.dto.ItemDto;
import gateway.dto.RequestCommentDto;
import gateway.dto.ItemBodyDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<ItemDto> add(@Valid @RequestBody ItemBodyDto itemBodyDto,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на добавление элемента: {}, для пользователя с ID {}", itemBodyDto, userId);
        return itemClient.add(itemBodyDto, userId);
    }

    @PatchMapping("/{itemsId}")
    public ResponseEntity<ItemDto> update(@PathVariable long itemsId,
                                          @Valid @RequestBody ItemBodyDto itemBodyDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление элемента с ID {}: {}, для пользователя с ID {}", itemsId, itemBodyDto, userId);
        return itemClient.update(itemsId, itemBodyDto, userId);
    }

    @GetMapping("/{itemsId}")
    public ResponseEntity<ItemDto> getById(@PathVariable long itemsId) {
        log.info("Запрос на получение элемента с ID {}", itemsId);
        return itemClient.getById(itemsId);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение элементов для владельца с ID {}", userId);
        return itemClient.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на поиск элементов по тексту '{}' для пользователя с ID {}", text, userId);
        return itemClient.search(text, userId);
    }

    @PostMapping("{itemId}/comment")
    @Validated
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId,
                                                 @Valid @RequestBody RequestCommentDto requestCommentDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление комментария к элементу с ID {}: {}, для пользователя с ID {}", itemId, requestCommentDto, userId);
        return itemClient.addComment(itemId, requestCommentDto, userId);
    }
}