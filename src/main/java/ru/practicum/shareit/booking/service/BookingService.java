package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.util.List;


public interface BookingService {

    BookingDto addBooking(RequestBookingDto requestBookingDto, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId);

    List<BookingDto> getUserBookings(Long userId, State state);

    List<BookingDto> getOwnerBookings(Long ownerId, State state);

    List<BookingDto> getAllBookings();
}
