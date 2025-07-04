package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

	/*
	Сергей Привет вроде все поправил. Спасибо за правки и советы. Буду ждать ОС. Хороших тебе выходных)


	 @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.booker.id = :userId
        AND b.item.id = :itemId
        AND b.status = APPROVED
        AND b.end < CURRENT_TIMESTAMP
        """)
    Boolean existsPastBookingsByBookerIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);


		Делаю такой запрос и он не отрабатывает правильно, как будто не видит время текущее.

		Пришлось добавить параметром LocalDateTime now
	 */
}