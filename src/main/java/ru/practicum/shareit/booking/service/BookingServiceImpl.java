package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dal.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingTimeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingDto addBooking(RequestBookingDto requestBookingDto, Long userId) {
        User booker = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemStorage.findById(requestBookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        User owner = item.getOwner();
        if (owner.equals(booker)) {
            throw new ValidationException("Владелец не может бронировать свою вещь ");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item не доступен к брони");
        }
        if (bookingStorage.timeCrossingCheck(item.getId(), requestBookingDto.getStart(), requestBookingDto.getEnd())) {
            throw new BookingTimeException("Пересечение по времени с уже подтвержденным бронированием");
        }
        Booking booking = BookingMapper.mapToBooking(requestBookingDto, item, booker);
        booking = bookingStorage.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        User owner = booking.getItem().getOwner();
        User booker = booking.getBooker();

        if (!booker.getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new ValidationException("Просмотр бронирования доступен только автору или владельцу вещи");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Подтверждать бронирование может только владелец вещи");
        }
        userStorage.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Бронирование не в режиме ожидания");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingStorage.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, State state) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Booking> userBookings = switch (state) {
            case CURRENT -> bookingStorage.findByBookerIdAndStatusAndEndAfter(userId, Status.APPROVED, LocalDateTime.now());
            case WAITING -> bookingStorage.findByBookerIdAndStatus(userId, Status.WAITING);
            case PAST -> bookingStorage.findByBookerIdAndStatusAndEndBefore(userId, Status.APPROVED, LocalDateTime.now());
            case REJECTED -> bookingStorage.findByBookerIdAndStatus(userId, Status.REJECTED);
            case FUTURE -> bookingStorage.findByBookerIdAndStartAfter(userId, LocalDateTime.now());
            default -> bookingStorage.findByBookerIdOrderByStartDesc(userId);
        };
        return userBookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, State state) {
        userStorage.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Booking> userBookings = switch (state) {
            case CURRENT -> bookingStorage.findByBookerIdAndStatusAndEndAfter(ownerId, Status.APPROVED, LocalDateTime.now());
            case WAITING -> bookingStorage.findByBookerIdAndStatus(ownerId, Status.WAITING);
            case PAST -> bookingStorage.findByBookerIdAndStatusAndEndBefore(ownerId, Status.APPROVED, LocalDateTime.now());
            case REJECTED -> bookingStorage.findByBookerIdAndStatus(ownerId, Status.REJECTED);
            case FUTURE -> bookingStorage.findByBookerIdAndStartAfter(ownerId, LocalDateTime.now());
            default -> bookingStorage.findByBookerIdOrderByStartDesc(ownerId);
        };
        return userBookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllBookings() {
        List<Booking> bookings = bookingStorage.findAll();
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }
}
