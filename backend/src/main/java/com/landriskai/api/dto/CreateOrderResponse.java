package com.landriskai.api.dto;

import com.landriskai.domain.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderResponse {
    private Long orderId;
    private Integer amountPaise;
    private OrderStatus status;
    private String nextAction; // for MVP: call mock-pay
}
