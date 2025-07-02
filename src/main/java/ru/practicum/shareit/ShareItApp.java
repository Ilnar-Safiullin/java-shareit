package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

	/*
	Сергей привет я пушу так как уже среда и надо же будет еще правки делать. До 4 утра сижу каждый день, очень долго
	боролся с методом addComment() в ItemServiceImpl. Постман тесты создают бронирование с разницей в 1 секунду от текущего
	времени, и там есть тест "Comment approved booking" он похоже в этом тесте создает бронирование с секундой раньше чем
	текущее время, и я пытался это отловить при создании букинга но у меня из за этого валился уже другой тест "Comment past booking".
	В итоге я смог победить постман тест только пакистанской системой костылями(оставил комменты в методе addComment() в ItemServiceImpl).
	Я и в группе спрашивал и ребят дергал у которых этот тест прошел, и их запросы к Бд копировал но у меня тест валился всеровно.
	Спасибо жду рекомендаций и правок. Извини что тебе приходится читать этот ужас. Хорошего тебе Дня!

	P.s. пытаюсь пробиться на стажировку в Aston, ближе к середине июля будет интервью.

	 */
}