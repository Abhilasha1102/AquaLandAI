package com.landriskai.api;

import com.landriskai.api.dto.CreateOrderRequest;
import com.landriskai.api.dto.CreateOrderResponse;
import com.landriskai.api.dto.MockPayResponse;
import com.landriskai.config.LandRiskAiProperties;
import com.landriskai.domain.OrderStatus;
import com.landriskai.entity.OrderEntity;
import com.landriskai.entity.ReportEntity;
import com.landriskai.entity.SearchCacheEntity;
import com.landriskai.service.OrderService;
import com.landriskai.service.ReportService;
import com.landriskai.repo.SearchCacheRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ReportService reportService;
    private final SearchCacheRepository searchCacheRepository;
    private final LandRiskAiProperties props;

    public OrderController(
            OrderService orderService,
            ReportService reportService,
            SearchCacheRepository searchCacheRepository,
            LandRiskAiProperties props
    ) {
        this.orderService = orderService;
        this.reportService = reportService;
        this.searchCacheRepository = searchCacheRepository;
        this.props = props;
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
    public MockPayResponse mockPay(@PathVariable Long orderId, @RequestParam(required = false) String paymentRef) throws Exception {
        String resolvedPaymentRef = (paymentRef == null || paymentRef.isBlank())
                ? "MOCK_UPI_TXN_" + orderId + "_" + System.currentTimeMillis()
                : paymentRef;
        orderService.markPaid(orderId, resolvedPaymentRef);
        ReportEntity report = reportService.generateAndDeliver(orderId);
        if (report.getReferenceNo() == null || report.getReferenceNo().isBlank() || "PENDING".equalsIgnoreCase(report.getReferenceNo())) {
            report = reportService.ensureReferenceAndArtifactsByReportId(report.getId());
        }

        String base = props.getLinks().getBaseUrl();
        return MockPayResponse.builder()
                .orderId(orderId)
                .status(OrderStatus.DELIVERED)
                .reportId(report.getId())
                .referenceNo(report.getReferenceNo())
                .downloadUrl(base + "/api/reports/" + report.getId() + "/download")
                .verifyUrl(base + "/api/reports/" + report.getId() + "/verify?code=" + report.getVerificationCode())
                .build();
    }

    /**
     * Check cache eligibility for discount
     * IMPORTANT: Discount (₹5 / 80% off) ONLY applies if same user (email + whatsapp) searches again
     * Different users searching same land = full price ₹25 (no discount)
     * This maximizes revenue while rewarding returning customers
     */
    @GetMapping("/cache/check")
    public ResponseEntity<?> checkCache(
            @RequestParam String khata,
            @RequestParam String khesra,
            @RequestParam String district,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String whatsapp) {
        
        // First check: Is there ANY cache for this land?
        SearchCacheEntity anyCache = searchCacheRepository.findValidByLandIdentifiers(khata, khesra, district);
        
        if (anyCache == null) {
            // No cache exists - first time this land is searched
            return ResponseEntity.notFound().build();
        }
        
        // Cache exists, now check if it's the SAME USER
        boolean isSameUser = false;
        if (email != null && whatsapp != null && 
            email.equals(anyCache.getLastUserEmail()) && 
            whatsapp.equals(anyCache.getLastUserWhatsapp())) {
            // Same user - eligible for discount (80% off)
            isSameUser = true;
        }
        
        // Return response indicating discount eligibility
        return ResponseEntity.ok().body(new CacheCheckResponse(
                anyCache.getId(),
                anyCache.getRiskBand(),
                anyCache.getRiskScore(),
                anyCache.getReusageCount(),
                isSameUser,  // true = ₹5, false = ₹25
                isSameUser ? 500 : 2500,  // Price in paise
                anyCache.getPdfPath()
        ));
    }
    
    /**
     * Response DTO for cache check endpoint
     */
    public static class CacheCheckResponse {
        public Long cacheId;
        public String riskBand;
        public Integer riskScore;
        public Integer reusageCount;
        public boolean discountEligible;  // true if same user, false if different user
        public Integer pricePaise;        // 500 (₹5) if discount eligible, 2500 (₹25) if not
        public String pdfPath;           // Path to cached report PDF
        
        public CacheCheckResponse(Long cacheId, String riskBand, Integer riskScore, 
                                 Integer reusageCount, boolean discountEligible, 
                                 Integer pricePaise, String pdfPath) {
            this.cacheId = cacheId;
            this.riskBand = riskBand;
            this.riskScore = riskScore;
            this.reusageCount = reusageCount;
            this.discountEligible = discountEligible;
            this.pricePaise = pricePaise;
            this.pdfPath = pdfPath;
        }
    }
}
