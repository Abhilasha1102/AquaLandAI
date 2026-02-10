# Quick Reference Guide – LandRiskAI Backend

**Last Updated:** February 10, 2026  
**For:** Developers & Architects

---

## 🚀 Start Development in 5 Minutes

### 1. Clone & Setup
```bash
cd landriskai-bihar/backend
./gradlew clean build
./gradlew bootRun
```

### 2. Access Application
```
API:        http://localhost:8080
Swagger:    http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console (user: sa)
```

### 3. Create Your First Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "district": "Patna",
    "circle": "Patna City",
    "village": "Digha",
    "khata": "123",
    "khesra": "45",
    "whatsappNumber": "9876543210"
  }'
```

---

## 📁 Project Structure Quick Map

```
backend/
├── src/main/java/com/landriskai/
│   ├── api/              ← REST Controllers & DTOs
│   ├── config/           ← Configuration & Exception Handling
│   ├── domain/           ← Enums (OrderStatus, RiskBand)
│   ├── entity/           ← JPA Entities (7 tables)
│   ├── exception/        ← Custom Exceptions
│   ├── notify/           ← WhatsApp Integration
│   ├── pdf/              ← PDF Generation
│   ├── repo/             ← Repository Interfaces
│   ├── risk/             ← Risk Scoring Engine
│   ├── service/          ← Business Logic (Order, Report)
│   ├── ui/               ← UI Controllers
│   ├── util/             ← Utilities (Encryption, Validation)
│   └── LandRiskAiApplication.java ← Main Application
│
├── src/main/resources/
│   ├── application.yml   ← Configuration
│   ├── templates/        ← HTML pages
│   └── data.sql          ← Test data (optional)
│
└── build.gradle          ← Dependencies & Build Config
```

---

## 🔑 Key Entities (Database Tables)

| Entity | Purpose | Key Fields |
|--------|---------|-----------|
| **OrderEntity** | Order lifecycle | orderId, status, khata, khesra, whatsappNumber |
| **ReportEntity** | Generated reports | reportId, riskBand, riskScore, pdfPath |
| **UserEntity** | User accounts | phoneEncrypted, phoneHash, userType, consent |
| **PaymentEntity** | Payment tracking | paymentGatewayRef, status, confirmedAt |
| **NotificationEntity** | Delivery tracking | notificationType, status, retryCount |
| **ResellerEntity** | Reseller accounts | businessName, commissionType, walletBalance |
| **AuditLogEntity** | Admin audit trail | adminUserId, actionType, entityType, entityId |

---

## 🔐 Security Quick Reference

### Phone Encryption
```java
// Encrypt
String encrypted = EncryptionUtil.encrypt("+919876543210");

// Decrypt
String phone = EncryptionUtil.decrypt(encrypted);

// Hash for lookup
String hash = EncryptionUtil.hash("+919876543210");
```

### Input Validation
```java
// Validate phone
if (!ValidationUtil.isValidPhone(phone)) {
    throw new InputValidationException("phone", "Invalid phone format");
}

// Validate Khata
if (!ValidationUtil.isValidKhata(khata)) {
    throw new InputValidationException("khata", "Invalid Khata format");
}

// Sanitize feedback
String safe = ValidationUtil.sanitizeFeedback(userFeedback);
```

### Error Handling
```java
// Custom exception
throw new OrderNotFoundException(orderId);

// Automatic handling by GlobalExceptionHandler
// Response format: { traceId, timestamp, status, errorCode, message, path }
```

---

## 📊 Common Database Queries

### Find Order by ID
```java
Optional<OrderEntity> order = orderRepository.findById(orderId);
```

### Find All Pending Payments
```java
List<PaymentEntity> pending = paymentRepository.findByStatus(PaymentStatus.PENDING);
```

### Find User by Phone Hash
```java
Optional<UserEntity> user = userRepository.findByPhoneHash(phoneHash);
```

### Find Expired Notifications (for retry)
```java
List<NotificationEntity> retries = notificationRepository.findPendingRetries(Instant.now());
```

### Audit Actions by Admin
```java
List<AuditLogEntity> actions = auditRepository.findByAdminUserId(adminUserId);
```

---

## 🛠️ Configuration Quick Reference

### application.yml (Key Settings)

```yaml
# Database Connection
spring.datasource.url: jdbc:postgresql://localhost:5432/landriskai
spring.jpa.hibernate.ddl-auto: validate  # Use Flyway in prod

# Encryption Key (LOAD FROM ENV!)
landriskai.security.encryptionKey: ${ENCRYPTION_KEY}

# Payments
landriskai.payment.gateway: RAZORPAY
landriskai.payment.apiKey: ${PAYMENT_API_KEY}

# WhatsApp (disable in dev)
landriskai.whatsapp.enabled: false

# Email (optional)
landriskai.email.enabled: false

# CORS (frontend URL)
landriskai.security.corsAllowedOrigins: "http://localhost:3000"
```

### Run with Profile
```bash
# Development
java -jar app.jar --spring.profiles.active=dev

# Production
java -jar app.jar --spring.profiles.active=prod
```

---

## 🧪 Testing Quick Snippets

### Test Order Creation
```java
@Test
public void testCreateOrder() {
    CreateOrderRequest req = CreateOrderRequest.builder()
        .district("Patna")
        .circle("Patna City")
        .village("Digha")
        .khata("123")
        .khesra("45")
        .whatsappNumber("9876543210")
        .build();
    
    OrderEntity order = orderService.createOrder(req);
    assertNotNull(order.getId());
    assertEquals(OrderStatus.CREATED, order.getStatus());
}
```

### Test Payment Reconciliation
```java
@Test
public void testPaymentReconciliation() {
    PaymentEntity payment = PaymentEntity.builder()
        .paymentGateway("RAZORPAY")
        .status(PaymentStatus.PENDING)
        .build();
    
    paymentRepository.save(payment);
    
    List<PaymentEntity> expired = paymentRepository.findExpiredPendingPayments(Instant.now());
    assertTrue(expired.size() > 0);
}
```

---

## 📝 Common Development Tasks

### Add New API Endpoint

**Step 1:** Create DTO
```java
@Data
public class MyRequest {
    @NotBlank
    private String field;
}
```

**Step 2:** Add Controller Method
```java
@PostMapping("/my-endpoint")
public ResponseEntity<MyResponse> myEndpoint(@Valid @RequestBody MyRequest req) {
    // Use service layer
    return ResponseEntity.ok(response);
}
```

**Step 3:** Test with curl
```bash
curl -X POST http://localhost:8080/api/my-endpoint \
  -H "Content-Type: application/json" \
  -d '{"field": "value"}'
```

### Add New Database Table

**Step 1:** Create Entity
```java
@Entity
@Table(name = "my_table")
public class MyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

**Step 2:** Create Repository
```java
@Repository
public interface MyRepository extends JpaRepository<MyEntity, Long> {
}
```

**Step 3:** Create Migration
```sql
-- V2__create_my_table.sql
CREATE TABLE my_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ...
);
```

### Debug Payment Flow

1. **Check Payment Status:**
```sql
SELECT * FROM lr_payment WHERE order_id = 123;
```

2. **Check Webhook Status:**
```sql
SELECT webhook_received, status FROM lr_payment WHERE order_id = 123;
```

3. **Check Reconciliation:**
```sql
SELECT reconciliation_status, last_reconciliation_at FROM lr_payment WHERE order_id = 123;
```

4. **View Logs:**
```bash
tail -f logs/landriskai.log | grep -i payment
```

---

## 🔍 Performance Tips

### Database Optimization
- ✅ Use indexes on frequently queried columns
- ✅ Use pagination for large result sets
- ✅ Use lazy loading for relationships
- ✅ Use batch operations for bulk inserts

### API Optimization
- ✅ Use response DTOs (not entities)
- ✅ Enable response compression (gzip)
- ✅ Implement caching for reference data
- ✅ Use async processing for long operations

### Example: Pagination
```java
@GetMapping("/orders")
public Page<OrderEntity> getOrders(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    return orderRepository.findAll(pageable);
}
```

---

## 🚨 Troubleshooting Quick Fixes

| Problem | Solution |
|---------|----------|
| Port 8080 in use | `lsof -i :8080` then `kill -9 <PID>` |
| H2 console not opening | Check `spring.h2.console.enabled: true` in yml |
| Database connection error | Verify DB URL, user, password in properties |
| Validation failing | Check @Valid and @NotBlank annotations |
| Encryption key error | Set ENCRYPTION_KEY environment variable |
| Phone validation fails | Ensure 10-13 digits, no special characters |
| Payment webhook not working | Check Razorpay API secret and webhook URL |

---

## 📞 Important Contacts

| Component | Responsible | Notes |
|-----------|-------------|-------|
| Database | DevOps | PostgreSQL setup & maintenance |
| API Keys | DevOps | Razorpay, WhatsApp, Email credentials |
| Infrastructure | DevOps | Docker, Kubernetes, monitoring |
| Frontend | Frontend Team | React app at port 3000 |
| Deployment | DevOps | CI/CD pipeline, production releases |

---

## 📚 Quick Documentation Links

- **[README.md](README.md)** – Complete setup & architecture
- **[ANALYSIS_AND_FIXES.md](ANALYSIS_AND_FIXES.md)** – Issues & solutions
- **[PROJECT_COMPLETION_REPORT.md](PROJECT_COMPLETION_REPORT.md)** – Detailed refactoring report
- **[Swagger UI](http://localhost:8080/swagger-ui.html)** – Interactive API docs
- **[H2 Console](http://localhost:8080/h2-console)** – Database browser

---

## ✅ Pre-Commit Checklist

- [ ] Code compiles: `./gradlew build`
- [ ] Tests pass: `./gradlew test`
- [ ] No security warnings: `./gradlew dependencyCheck`
- [ ] Code formatted: Consistent with existing code
- [ ] Comments added: Complex logic explained
- [ ] Javadoc updated: Public methods documented
- [ ] Git message clear: Descriptive commit message

---

## 🎯 Next Immediate Tasks

1. Implement `PaymentService` (payments processing)
2. Implement `NotificationService` (WhatsApp/Email/SMS)
3. Create payment webhook controller
4. Create admin dashboard APIs
5. Add comprehensive unit tests
6. Set up database migrations (Flyway)
7. Configure CI/CD pipeline

---

**Happy coding! 🚀**
