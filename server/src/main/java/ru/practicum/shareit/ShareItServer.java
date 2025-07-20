package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServer {

	public static void main(String[] args) {
		SpringApplication.run(ShareItServer.class, args);
	}

	/*
	Сергей Привет, ничерта не успеваю. Прям бегом бегом ночами сидел собирал. Из интересного почему то в интеграционных тестах
	 начало сыпать LazyException. И я думал сперва просто на метод в классе воткнуть @Transactional аннотацию чтобы их не было
	 но потом нашел как обмануть в тестах, если сразу после получения результата взять нужный параметр, и использовать его в тесте на
	 сравнение, то лези ошибки нет. Но незнаю насколько это правильно. Класс BookingServiceImplIntegrationTest 64 и 83 строки.


	 Буду рад обратной связи. Хороших Тебе Выходных:)
	 */
}