package com.example.order_service.client;

import groovy.util.logging.Slf4j;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Slf4j
public interface InventoryClient {


    Log log = LogFactory.getLog(InventoryClient.class);

    @GetExchange("api/inventory")
    @CircuitBreaker(name="inventory",fallbackMethod = "fallbackMethod")
    @Retry(name="inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default boolean fallbackMethod(String skuCode, Integer quantity, Throwable throwable){
        log.info("Cannot get inventory for skuCode "+skuCode+", failure reason: "+throwable.getMessage());
        return false;
    }
}
