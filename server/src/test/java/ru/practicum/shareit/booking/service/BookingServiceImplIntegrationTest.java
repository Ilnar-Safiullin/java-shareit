package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dal.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private BookingStorage bookingStorage; // репозиторий бронирований

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();

        String timestamp = String.valueOf(System.currentTimeMillis()); //H2 на гитхаб почемуто не ролбекает базу, а локально все норм было, пришлось добавить
        owner = new User(null, "Owner", "owner_" + timestamp + "@example.com");
        booker = new User(null, "User", "user_" + timestamp + "@example.com");
        userStorage.save(owner);
        userStorage.save(booker);

        item = new Item(null, owner, "Item 1", "Description 1", true, null);
        itemStorage.save(item);

        booking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, Status.APPROVED);
        bookingStorage.save(booking);
    }

    @Test
    @Rollback
    public void testGetUserBookings() {
        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), State.ALL);
        Long id = bookings.getFirst().getItem().getId();

        assertThat(bookings).isNotEmpty();
        assertThat(id.equals(item.getId()));
    }

    @Test
    @Rollback
    public void testGetOwnerBookings() {

        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), State.ALL);
        Long id = bookings.getFirst().getItem().getId();

        assertThat(bookings).isNotEmpty();
        assertThat(id.equals(item.getId()));
    }

    @Test
    @Rollback
    public void testGetAllBookings() {
        Booking booking2 = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item, booker, Status.WAITING);
        bookingStorage.save(booking2);

        List<BookingDto> allBookings = bookingService.getAllBookings();

        assertThat(allBookings).hasSize(2);
    }

    @Test
    @Rollback
    public void testGetUserBookings_UserNotFound() {
        Long nonExistentUserId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getUserBookings(nonExistentUserId, State.ALL);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @Rollback
    public void testGetOwnerBookings_NoItems() {
        User userWithoutItems = new User(null, "NoItemsUser", "noitems@example.com");
        userStorage.save(userWithoutItems);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getOwnerBookings(userWithoutItems.getId(), State.ALL);
        });

        assertThat(exception.getMessage()).isEqualTo("У данного пользователя нет предметов");
    }
}