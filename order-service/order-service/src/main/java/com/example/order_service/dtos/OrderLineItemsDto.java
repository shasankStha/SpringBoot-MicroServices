package com.example.order_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItemsDto {
    private Long id;
    private String skuCode;
    private Double price;
    private Integer quantity;
}
