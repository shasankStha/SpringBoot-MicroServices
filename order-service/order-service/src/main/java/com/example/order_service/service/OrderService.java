package com.example.order_service.service;

import com.example.order_service.dtos.InventoryResponse;
import com.example.order_service.dtos.OrderLineItemsDto;
import com.example.order_service.dtos.OrderRequest;
import com.example.order_service.event.OrderPlacedEvent;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderLineItems;
import com.example.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<String> placeOrder(OrderRequest orderRequest) throws IllegalAccessException, InterruptedException {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .build();
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> (OrderLineItems) mapToDto(orderLineItemsDto))
                .toList();

        order.setOrderLineItemsList(orderLineItems);


        List<String> skuCodes = order.getOrderLineItemsList().stream().map(orderLineItem -> orderLineItem.getSkuCode()).toList();

        //Call Inventory Service, and Place order if product is in stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build( ))
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if(Thread.currentThread().isInterrupted()){
            throw new InterruptedException("Task was interrupted due to timeout");
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if(allProductsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return CompletableFuture.supplyAsync(()->"Order Placed Successfully");
        }else{
            throw  new IllegalAccessException("Product is not in Stock.");
        }
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, Throwable throwable){
        return CompletableFuture.supplyAsync(()->"Something went wrong, please order after sometime!");
    }

    private Object mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
        return orderLineItems;
    }

}
