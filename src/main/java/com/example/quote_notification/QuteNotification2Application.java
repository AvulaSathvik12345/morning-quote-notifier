package com.example.quote_notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QuteNotification2Application {

	public static void main(String[] args) {
		SpringApplication.run(QuteNotification2Application.class, args);
	}

}
