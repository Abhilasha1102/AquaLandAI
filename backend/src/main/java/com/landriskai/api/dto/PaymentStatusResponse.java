package com.landriskai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    private Long orderId;
    private String paymentStatus;
    private Integer amountPaise;
    private String paymentRef;
    private Boolean reportReady;
    private Long reportId;
}
