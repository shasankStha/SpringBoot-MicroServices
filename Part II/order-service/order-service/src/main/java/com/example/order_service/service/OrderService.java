package com.example.order_service.service;

import com.example.order_service.client.InventoryClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.event.OrderPlacedEvent;
import com.example.order_service.model.Order;

import com.example.order_service.repository.OrderRepository;
import groovy.util.logging.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
public class OrderService {

    private static final Log log = LogFactory.getLog(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate; //key ins name of the topic, and value is type of object we are sending

    public void placeOrder(OrderRequest orderRequest){
        if(inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity())) {
            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .skuCode(orderRequest.skuCode())
                    .price(orderRequest.price())
                    .quantity(orderRequest.quantity())
                    .email(orderRequest.email())
                    .build();
            orderRepository.save(order);

            //Send message to Kafka topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(),order.getEmail());
            log.info("Started sending message to Kafka template");
            kafkaTemplate.send("order-placed",orderPlacedEvent);
            log.info("Ended sending message to Kafka template");

        }else{
            throw new RuntimeException("Product with SkuCode "+orderRequest.skuCode()+" is not in Stock");
        }
    }

}
