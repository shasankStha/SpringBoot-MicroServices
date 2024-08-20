package com.example.notification_service.service;

import com.example.notification_service.event.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent){
        log.info("Order receiver {}",orderPlacedEvent.getOrderNumber());
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("abc@gmail.com");
            messageHelper.setTo(orderPlacedEvent.getEmail());
            messageHelper.setSubject("Your order is placed successfully");
            messageHelper.setText(String.format("""
                    Hi,
                    
                    Your order with order number %s is now placed successfully.
                    
                    Best regards,
                    Spring MicroService
                    """,
                    orderPlacedEvent.getOrderNumber()));
        };

        try{
            javaMailSender.send(messagePreparator);
            log.info("Order mail send");
        }catch (MailException e){
            log.error("Error sending mail", e);
            throw new RuntimeException("Error occurred while sending mail",e);
        }
    }
}
