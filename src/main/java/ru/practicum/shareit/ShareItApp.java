package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

	/*
	Сергй привет вроде все сделал.

	По ДТО
	У нас просто по условию задачи в ответ надо отдать ДТО без поля owner. Я вообще думаю что поле айди можно было бы
	скрывать при выдаче ДТО, но постман тесты требует поле айди чтобы было. Сделал одну ДТО для запросов где нет айди и
	поля owner, а на выдачу сделал ДТО с полем айди. Поле Owner не отдаю на выдачу так как по условиям задачи нужно его
	скрывать при выдаче.

	В репозиторий ДТО не попадают.

	P.s. чтото я намудрил с коммитом Amend
	 */
}
