package com.example.notification_service;

import com.example.notification_service.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic", id="notificationId")
	public void handelNotification(OrderPlacedEvent orderPlacedEvent){
		log.info("Order receiver {}",orderPlacedEvent.getOrderNumber());
		System.out.println("Order received "+orderPlacedEvent.getOrderNumber());
	}

}
