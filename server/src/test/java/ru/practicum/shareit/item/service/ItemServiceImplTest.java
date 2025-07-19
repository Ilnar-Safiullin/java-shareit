package ru.practicum.shareit.item.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dal.BookingStorage;
import ru.practicum.shareit.exception.BookingTimeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dal.CommentStorage;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemBodyDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.RequestStorage;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private RequestStorage requestStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemBodyDto itemBodyDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "username", "email@example.com");
        item = new Item(1L, user, "Item name", "Item description", true,null);
        itemBodyDto = new ItemBodyDto("Item name", "Item description", true, null);
    }

    @Test
    void add_ShouldReturnItemDto_WhenUserExists() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.add(1L, itemBodyDto);


        assertNotNull(result);
        assertEquals("Item name", result.getName());
        verify(userStorage, times(1)).findById(1L);
        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void update_ShouldReturnUpdatedItemDto_WhenItemExistsAndUserIsOwner() {
        Item oldItem = new Item(1L, user, "Old name", "Old description", true,null);

        when(itemStorage.findById(1L)).thenReturn(Optional.of(oldItem));
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(1L, itemBodyDto, 1L);

        assertNotNull(result);
        assertEquals("Item name", result.getName());
        verify(itemStorage).findById(1L);
        verify(itemStorage).save(any(Item.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenUserIsNotOwner() {
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.update(1L, itemBodyDto, 2L);
        });

        assertEquals("Вы не можете редактировать чужую вещь", exception.getMessage());
    }

    @Test
    void getById_ShouldReturnItemDto_WhenItemExists() {
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(commentStorage.findByItemId(1L)).thenReturn(List.of());

        ItemDto result = itemService.getById(1L);

        assertNotNull(result);
        assertEquals("Item name", result.getName());
        verify(itemStorage).findById(1L);
    }

    @Test
    void search_ShouldReturnEmptyList_WhenTextIsBlank() {
        List<ItemDto> result = itemService.search("");

        assertTrue(result.isEmpty());
        verify(itemStorage, never()).searchByText(anyString());
    }

    @Test
    void addComment_ShouldReturnCommentDto_WhenBookingExists() {
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        RequestCommentDto requestCommentDto = new RequestCommentDto("text");

        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(bookingStorage.existsPastBookingsByBookerIdAndItemId(anyLong(), anyLong(), any())).thenReturn(true);

        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, requestCommentDto, 1L);

        assertNotNull(result);
        verify(commentStorage).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowBookingTimeException_WhenNoPastBookingsExist() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(bookingStorage.existsPastBookingsByBookerIdAndItemId(anyLong(), anyLong(), any())).thenReturn(false);

        RequestCommentDto requestCommentDto = new RequestCommentDto("Great item!");

        Exception exception = assertThrows(BookingTimeException.class, () -> {
            itemService.addComment(1L, requestCommentDto, 1L);
        });

        assertEquals("Нет законченных бронирований", exception.getMessage());
    }
}