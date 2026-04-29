package com.backend.event_driven_order_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EventDrivenOrderSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventDrivenOrderSystemApplication.class, args);
	}

}
