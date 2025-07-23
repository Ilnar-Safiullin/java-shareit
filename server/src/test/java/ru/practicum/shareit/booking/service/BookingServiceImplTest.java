package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dal.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingTimeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime start = now.plusHours(1);
    private final LocalDateTime end = now.plusHours(2);

    @Test
    void addBooking_shouldCreateBookingSuccessfully() {
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = 2L;
        RequestBookingDto request = new RequestBookingDto(start, end, itemId);

        User booker = new User(userId, "Booker", "booker@example.com");
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(itemId, owner, "Item", "Description", true, null);
        Booking savedBooking = new Booking(1L, start, end, item, booker, Status.WAITING);

        when(userStorage.findById(userId)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingStorage.timeCrossingCheck(itemId, start, end)).thenReturn(false);
        when(bookingStorage.save(any(Booking.class))).thenReturn(savedBooking);

        BookingDto result = bookingService.addBooking(request, userId);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
        assertEquals(itemId, result.getItem().getId());
        assertEquals(userId, result.getBooker().getId());

        verify(userStorage).findById(userId);
        verify(itemStorage).findById(itemId);
        verify(bookingStorage).timeCrossingCheck(itemId, start, end);
        verify(bookingStorage).save(any(Booking.class));
    }

    @Test
    void addBooking_shouldThrowWhenUserNotFound() {
        Long userId = 99L;
        RequestBookingDto request = new RequestBookingDto(start, end, 1L);

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(request, userId));
    }

    @Test
    void addBooking_shouldThrowWhenItemNotFound() {
        Long userId = 1L;
        Long itemId = 99L;
        RequestBookingDto request = new RequestBookingDto(start, end, itemId);

        when(userStorage.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(request, userId));
    }

    @Test
    void addBooking_shouldThrowWhenOwnerBooksOwnItem() {
        Long userId = 1L;
        Long itemId = 1L;
        RequestBookingDto request = new RequestBookingDto(start, end, itemId);

        User owner = new User(userId, "Owner", "owner@example.com");
        Item item = new Item(itemId, owner, "Item", "Description", true, null);

        when(userStorage.findById(userId)).thenReturn(Optional.of(owner));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(request, userId));
    }

    @Test
    void addBooking_shouldThrowWhenItemNotAvailable() {
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = 2L;
        RequestBookingDto request = new RequestBookingDto(start, end, itemId);

        User booker = new User(userId, "Booker", "booker@example.com");
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(itemId, owner, "Item", "Description", false, null);

        when(userStorage.findById(userId)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(request, userId));
    }

    @Test
    void addBooking_shouldThrowWhenTimeCrossing() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = 2L;
        RequestBookingDto request = new RequestBookingDto(start, end, itemId);

        User booker = new User(userId, "Booker", "booker@example.com");
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(itemId, owner, "Item", "Description", true, null);

        when(userStorage.findById(userId)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingStorage.timeCrossingCheck(itemId, start, end)).thenReturn(true);

        assertThrows(BookingTimeException.class, () -> bookingService.addBooking(request, userId));
    }

    @Test
    void getBookingById_shouldReturnBookingForBooker() {
        Long bookingId = 1L;
        Long bookerId = 1L;
        Long ownerId = 2L;

        User booker = new User(bookerId, "Booker", "booker@example.com");
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);
        Booking booking = new Booking(bookingId, start, end, item, booker, Status.WAITING);

        when(userStorage.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(bookingId, bookerId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
    }

    @Test
    void getBookingById_shouldReturnBookingForOwner() {
        Long bookingId = 1L;
        Long bookerId = 1L;
        Long ownerId = 2L;

        User booker = new User(bookerId, "Booker", "booker@example.com");
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);
        Booking booking = new Booking(bookingId, start, end, item, booker, Status.WAITING);

        when(userStorage.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(bookingId, ownerId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
    }

    @Test
    void getBookingById_shouldThrowWhenUnauthorized() {
        Long bookingId = 1L;
        Long bookerId = 1L;
        Long ownerId = 2L;
        Long otherUserId = 3L;

        User booker = new User(bookerId, "Booker", "booker@example.com");
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);
        Booking booking = new Booking(bookingId, start, end, item, booker, Status.WAITING);

        when(userStorage.findById(otherUserId)).thenReturn(Optional.of(new User(otherUserId, "Other", "other@example.com")));
        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getBookingById(bookingId, otherUserId));
    }

    @Test
    void approveBooking_shouldApproveSuccessfully() {
        Long bookingId = 1L;
        Long ownerId = 2L;

        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);
        Booking booking = new Booking(bookingId, start, end, item, new User(1L, "Booker", "booker@example.com"), Status.WAITING);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userStorage.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);


        BookingDto result = bookingService.approveBooking(bookingId, true, ownerId);

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_shouldRejectSuccessfully() {
        Long bookingId = 1L;
        Long ownerId = 2L;

        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);
        Booking booking = new Booking(bookingId, start, end, item, new User(1L, "Booker", "booker@example.com"), Status.WAITING);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userStorage.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(bookingId, false, ownerId);

        assertNotNull(result);
        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void approveBooking_shouldThrowWhenNotOwner() {
        Long bookingId = 1L;
        Long ownerId = 2L;
        Long otherUserId = 3L;

        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);
        Booking booking = new Booking(bookingId, start, end, item, new User(1L, "Booker", "booker@example.com"), Status.WAITING);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(bookingId, true, otherUserId));
    }

    @Test
    void approveBooking_shouldThrowWhenNotWaiting() {
        Long bookingId = 1L;
        Long ownerId = 2L;

        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);
        Booking booking = new Booking(bookingId, start, end, item, new User(1L, "Booker", "booker@example.com"), Status.APPROVED);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userStorage.findById(ownerId)).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(bookingId, true, ownerId));
    }

    @Test
    void getUserBookings_shouldReturnAllBookings() {
        Long userId = 1L;

        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);

        Booking booking = new Booking(1L, start, end, item, new User(1L, "Booker", "booker@example.com"), Status.APPROVED);

        when(userStorage.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingStorage.findByBookerIdOrderByStartDesc(userId)).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getUserBookings(userId, State.ALL);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getBooker().getId());
    }

    @Test
    void getOwnerBookings_shouldReturnAllBookings() {
        Long ownerId = 2L;
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);

        Booking booking = new Booking(1L, start, end, item, new User(1L, "Booker", "booker@example.com"), Status.APPROVED);

        when(userStorage.findById(ownerId)).thenReturn(Optional.of(new User()));
        when(itemStorage.findByOwnerId(ownerId)).thenReturn(Collections.singletonList(new Item()));
        when(bookingStorage.findByItemOwnerIdOrderByStartDesc(ownerId)).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(ownerId, State.ALL);

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_shouldThrowWhenNoItems() {
        Long ownerId = 2L;

        when(userStorage.findById(ownerId)).thenReturn(Optional.of(new User()));
        when(itemStorage.findByOwnerId(ownerId)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings(ownerId, State.ALL));
    }

    @Test
    void getAllBookings_shouldReturnAllBookings() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, owner, "Item", "Description", true, null);

        Booking booking = new Booking(1L, start, end, item, new User(1L, "Booker", "booker@example.com"), Status.APPROVED);

        when(bookingStorage.findAll()).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
    }
}