package com.landriskai.api.dto;

import com.landriskai.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockPayResponse {
    private Long orderId;
    private OrderStatus status;
    private Long reportId;
    private String downloadUrl;
    private String verifyUrl;
}
