package com.moviebooking.show;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableFeignClients
public class ShowServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowServiceApplication.class, args);
	}

}
