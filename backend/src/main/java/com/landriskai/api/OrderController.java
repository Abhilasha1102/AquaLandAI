package com.landriskai.api;

import com.landriskai.api.dto.CreateOrderRequest;
import com.landriskai.api.dto.CreateOrderResponse;
import com.landriskai.api.dto.MockPayResponse;
import com.landriskai.domain.OrderStatus;
import com.landriskai.entity.OrderEntity;
import com.landriskai.entity.ReportEntity;
import com.landriskai.service.OrderService;
import com.landriskai.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ReportService reportService;

    public OrderController(OrderService orderService, ReportService reportService) {
        this.orderService = orderService;
        this.reportService = reportService;
    }

    @PostMapping
    public CreateOrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
        OrderEntity order = orderService.createOrder(req);
        return CreateOrderResponse.builder()
                .orderId(order.getId())
                .amountPaise(order.getAmountPaise())
                .status(order.getStatus())
                .nextAction("Call POST /api/orders/" + order.getId() + "/mock-pay to simulate payment and generate report")
                .build();
    }

    @PostMapping("/{orderId}/mock-pay")
    public MockPayResponse mockPay(@PathVariable Long orderId, @RequestParam(defaultValue = "MOCK_UPI_TXN") String paymentRef) throws Exception {
        orderService.markPaid(orderId, paymentRef);
        ReportEntity report = reportService.generateAndDeliver(orderId);

        String base = "http://localhost:8080";
        return MockPayResponse.builder()
                .orderId(orderId)
                .status(OrderStatus.DELIVERED)
                .reportId(report.getId())
                .downloadUrl(base + "/api/reports/" + report.getId() + "/download")
                .verifyUrl(base + "/api/reports/" + report.getId() + "/verify?code=" + report.getVerificationCode())
                .build();
    }
}
