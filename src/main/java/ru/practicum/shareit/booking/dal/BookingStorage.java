package ru.practicum.shareit.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingStorage extends JpaRepository<Booking, Long> {

    //Сделал тут JOIN FETCH так как мы в ItemServiceImpl часто будем отправлять booking.getItem().getId() - что будет заставлять постоянно подгружать данные
    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.item.id IN :itemIds")
    List<Booking> findAllByItemIdIn(@Param("itemIds") List<Long> itemIds);

    // Получить все текущие не завершенные бронирования пользователя
    List<Booking> findByBookerIdAndStatusAndEndAfter(Long userId, Status status, LocalDateTime now);

    // Получить все бронирования пользователя с определенным статусом
    List<Booking> findByBookerIdAndStatus(Long userId, Status status);

    // Получить все завершенные бронирования пользователя
    List<Booking> findByBookerIdAndStatusAndEndBefore(Long userId, Status status, LocalDateTime now);

    // Получить все будущие бронирования пользователя
    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime now);

    // Получить все бронирования пользователя отсортированные по дате начала
    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    // Получить все текущие бронирования владельца предмета
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
            AND b.status = APPROVED
            AND b.start < :now
            AND b.end > :now
            """)
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now);

    // Получить все бронирования владельца предмета по статусу
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
            AND b.status = :status
            """)
    List<Booking> findInStatusByOwnerId(@Param("ownerId") Long ownerId,
                                        @Param("status") Status status);

    // Получить все завершенные бронирования владельца предмета
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
            AND b.status = APPROVED
            AND b.end < :now
            """)
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    // Получить все будущие бронирования владельца предмета
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
            AND b.start > :now
            """)
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId,
                                      @Param("now") LocalDateTime now);

    // Получить все бронирования владельца предмета отсортированные по дате начала
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
            ORDER BY b.start DESC
            """)
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId);

    // Проверить пересечение по времени
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.item.id = :itemId
        AND (
            (b.start < :endNewBooking AND b.end > :startNewBooking)
            OR
            (b.start > :startNewBooking AND b.end < :endNewBooking)
            OR
            (b.start < :endNewBooking AND b.end > :endNewBooking)
        )
        AND b.status = APPROVED
        """)
    Boolean timeCrossingCheck(@Param("itemId") Long itemId,
                              @Param("startNewBooking") LocalDateTime startNewBooking,
                              @Param("endNewBooking") LocalDateTime endNewBooking);

    // Проверить завершенное бронирование пользователя по UserId и ItemId
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.booker.id = :userId
        AND b.item.id = :itemId
        AND b.status = APPROVED
        AND b.end < :now
        """)
    Boolean existsPastBookingsByBookerIdAndItemId(@Param("userId") Long userId,
                                                  @Param("itemId") Long itemId,
                                                  @Param("now") LocalDateTime now);
}
