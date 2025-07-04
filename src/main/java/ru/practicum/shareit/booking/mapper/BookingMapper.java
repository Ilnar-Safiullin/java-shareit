package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking mapToBooking(RequestBookingDto requestBookingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(requestBookingDto.getStart());
        booking.setEnd(requestBookingDto.getEnd());
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.mapToItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}