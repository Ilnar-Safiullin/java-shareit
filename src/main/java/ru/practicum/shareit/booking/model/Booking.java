package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker", nullable = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
