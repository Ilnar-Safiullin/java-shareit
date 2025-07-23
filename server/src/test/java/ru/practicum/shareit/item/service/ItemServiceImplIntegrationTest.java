package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dal.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dal.CommentStorage;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private BookingStorage bookingStorage;

    @Autowired
    private CommentStorage commentStorage;

    private Long ownerId;

    @BeforeEach
    void setUp() {

        User owner = new User(null, "Owner Name", "owner@example.com");
        owner = userStorage.save(owner);
        ownerId = owner.getId();

        Item item1 = new Item(null, owner, "Item 1", "Description 1", true, null);
        itemStorage.save(item1);

        Item item2 = new Item(null, owner, "Item 2", "Description 2", true, null);
        itemStorage.save(item2);

        Booking booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusSeconds(2), item1, owner, Status.APPROVED);
        bookingStorage.save(booking);

        Comment comment = new Comment(null, "New Comment", item1, owner, LocalDateTime.now());
        commentStorage.save(comment);
    }

    @Test
    void testGetItemsByOwner() {
        List<ItemDto> items = itemService.getItemsByOwner(ownerId);

        assertThat(items).hasSize(2); // Проверяем, что два предмета возвращены

        ItemDto itemDto1 = items.get(0);
        assertThat(itemDto1.getName()).isEqualTo("Item 1");
        assertThat(itemDto1.getComments()).hasSize(1);
        assertThat(itemDto1.getLastBooking()).isNotNull();

        ItemDto itemDto2 = items.get(1);
        assertThat(itemDto2.getName()).isEqualTo("Item 2");
        assertThat(itemDto2.getComments()).isEmpty(); // Проверяем, что комментариев нет
    }
}

