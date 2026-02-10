package com.landriskai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landriskai.api.dto.CreateOrderRequest;
import com.landriskai.config.LandRiskAiProperties;
import com.landriskai.domain.OrderStatus;
import com.landriskai.entity.OrderEntity;
import com.landriskai.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    private final OrderRepository orderRepo;

    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest req) {
        OrderEntity order = OrderEntity.builder()
                .district(req.getDistrict().trim())
                .circle(req.getCircle().trim())
                .village(req.getVillage().trim())
                .khata(req.getKhata().trim())
                .khesra(req.getKhesra().trim())
                .ownerName(req.getOwnerName() == null ? null : req.getOwnerName().trim())
                .plotArea(req.getPlotArea() == null ? null : req.getPlotArea().trim())
                .whatsappNumber(req.getWhatsappNumber().trim())
                .amountPaise(2500)
                .status(OrderStatus.CREATED)
                .createTime(Instant.now())
                .updateTime(Instant.now())
                .build();

        return orderRepo.save(order);
    }

    @Transactional
    public OrderEntity markPaid(Long orderId, String paymentRef) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        order.setPaymentRef(paymentRef);
        order.setStatus(OrderStatus.PAID);
        order.setUpdateTime(Instant.now());
        return orderRepo.save(order);
    }

    @Transactional
    public OrderEntity updateStatus(Long orderId, OrderStatus status) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(status);
        order.setUpdateTime(Instant.now());
        return orderRepo.save(order);
    }
}
