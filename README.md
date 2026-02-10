# LandRiskAI – Complete Production-Ready Setup Guide

**Version:** 1.0  
**Last Updated:** February 10, 2026  
**Status:** Production-Ready MVP with Professional Standards

This is a **complete, enterprise-grade** land risk assessment system for Bihar and other Indian states.

---

## Quick Start (5 minutes)

### Prerequisites
- Java 17+
- Gradle 8.0+

### Run Locally

```bash
# Windows (PowerShell)
cd landriskai-bihar
gradle -p backend bootRun

# macOS/Linux
cd landriskai-bihar
./gradlew -p backend bootRun
```

**Access:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

---

## What's New in v1.0

### ✅ Database Enhancements
- **5 new entities**: UserEntity, ResellerEntity, AuditLogEntity, PaymentEntity, NotificationEntity
- **Proper relationships** with cascading and lazy loading
- **Database indexes** for performance optimization
- **Audit trails** for compliance (NFR-7)

### ✅ Security & Encryption
- **Phone encryption** (AES-256) at rest (NFR-5)
- **Token generation** and verification
- **CORS configuration** for frontend integration
- **Input validation** & sanitization (XSS/SQL injection prevention)

### ✅ Service Architecture
- **Payment Service** - Payment state tracking and reconciliation
- **Notification Service** - WhatsApp/Email/SMS delivery with retry logic
- **User Service** - User registration and account management
- **Reseller Service** - Reseller accounts and commission tracking
- **Audit Service** - Admin action logging

### ✅ Configuration Management
- **Environment profiles** (dev/prod)
- **Property-based configuration** with Spring ConfigurationProperties
- **Secrets management** via environment variables
- **Comprehensive logging** configuration

### ✅ Error Handling
- **Custom exception hierarchy** with HTTP status mapping
- **Centralized GlobalExceptionHandler** for consistent errors
- **Standardized error responses** with trace IDs
- **Validation error details** for debugging

### ✅ Testing & Documentation
- **ANALYSIS_AND_FIXES.md** - Technical debt analysis
- **Comprehensive README** - Setup and architecture
- **API documentation** - Swagger UI included
- **Security guide** - Best practices

---

## Key Features (MVP)

- ✅ Instant risk assessment (0-100 score, Red/Amber/Green)
- ✅ WhatsApp delivery + downloadable link
- ✅ Payment via UPI/Cards (Razorpay)
- ✅ Admin dashboard (metrics, search, resend)
- ✅ Reseller support (commissions, wallet)
- ✅ Audit logging (compliance-ready)
- ✅ Secure report links (7-day expiry)
- ✅ Encrypted phone numbers
- ✅ Notification delivery retry logic
- ✅ Payment reconciliation

---

## Core Endpoints

### 1. Create Order
```bash
POST /api/orders
{
  "district": "Patna",
  "circle": "Patna City",
  "village": "Digha",
  "khata": "123",
  "khesra": "45",
  "whatsappNumber": "9876543210"
}
```

### 2. Get Order Status
```bash
GET /api/orders/{orderId}
```

### 3. Verify Report
```bash
GET /api/reports/{reportId}/verify?code={code}
```

### 4. Rate Report
```bash
POST /api/reports/{reportId}/rate
{
  "rating": 4,
  "feedback": "Helpful report"
}
```

### 5. Admin Metrics
```bash
GET /api/admin/metrics?period=30d
```

### 6. Admin Search
```bash
GET /api/admin/transactions?phone=9876543210&from=2026-01-01&to=2026-02-10
```

### 7. Reseller Dashboard
```bash
GET /api/reseller/dashboard
```

---

## Database Schema

**7 main tables:**

| Table | Purpose |
|-------|---------|
| `lr_user` | User accounts with encrypted phones |
| `lr_order` | Orders with payment tracking |
| `lr_report` | Generated reports (1:1 with orders) |
| `lr_payment` | Payment state and reconciliation |
| `lr_notification` | Delivery tracking (WhatsApp/Email/SMS) |
| `lr_reseller` | Reseller accounts and commissions |
| `lr_audit_log` | Admin action audit trail |

See ANALYSIS_AND_FIXES.md for complete schema documentation.

---

## Configuration

### Environment Variables

```bash
# Encryption
ENCRYPTION_KEY=your-256-bit-base64-key

# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/landriskai
DATABASE_USER=landriskai
DATABASE_PASSWORD=secure-password

# Payment (Razorpay)
PAYMENT_API_KEY=rzp_test_xxxxx
PAYMENT_API_SECRET=xxxxx

# WhatsApp
WHATSAPP_API_KEY=xxxxx
WHATSAPP_API_SECRET=xxxxx

# Email (optional)
SMTP_HOST=smtp.gmail.com
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=app-password
```

### Application Profiles

```bash
# Development
java -jar app.jar --spring.profiles.active=dev

# Production
java -jar app.jar --spring.profiles.active=prod
```

---

## Architecture

### Layered Design

```
Controllers (REST APIs)
    ↓
Services (Business Logic)
    ↓
Repositories (Data Access)
    ↓
Database (PostgreSQL/H2)

External Services:
├── Razorpay (Payments)
├── WhatsApp Business (Notifications)
├── SendGrid (Email)
└── Twilio (SMS)
```

### Key Services

- **OrderService** - Order lifecycle management
- **ReportService** - Report generation pipeline
- **PaymentService** - Payment state tracking
- **NotificationService** - Delivery with retry
- **UserService** - User registration
- **ResellerService** - Reseller management
- **RiskEngine** - Risk scoring
- **AuditService** - Activity logging

---

## Security Features

✅ **Implemented:**
- AES-256 encryption for phone numbers
- SHA-256 hashing for secure lookups
- 7-day expiring report links
- CORS configuration
- Input validation & sanitization
- Comprehensive audit logging
- Role-based access control (RBAC) framework

🔄 **Coming Soon:**
- OAuth 2.0 authentication
- API key management
- Rate limiting
- DDoS protection

---

## Troubleshooting

### Problem: "Order not found"
- Check order ID
- Ensure order was created successfully

### Problem: "Report link expired"
- User can regenerate within 24 hours
- Default expiry: 7 days

### Problem: "WhatsApp delivery failed"
- Check phone format (10-13 digits)
- Verify WhatsApp credentials
- Check NotificationEntity for retry status

### Problem: "Payment verification failed"
- Verify Razorpay API secret
- Check payment webhook logs
- Run manual reconciliation

---

## Production Deployment

### Docker

```bash
docker build -t landriskai:latest -f Dockerfile .
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/landriskai \
  landriskai:latest
```

### Kubernetes

See `k8s/` directory for:
- Deployment manifests
- Service configuration
- ConfigMaps and Secrets
- Ingress rules

### Pre-deployment Checklist

- [ ] Use PostgreSQL (not H2)
- [ ] Configure all API keys
- [ ] Enable HTTPS/SSL
- [ ] Set up database backups
- [ ] Configure monitoring
- [ ] Load test the system
- [ ] Security audit
- [ ] Create incident runbooks

---

## Project Structure

```
backend/
├── src/main/java/com/landriskai/
│   ├── api/
│   │   ├── OrderController
│   │   ├── ReportController
│   │   ├── AdminController
│   │   ├── ResellerController
│   │   └── dto/
│   │       ├── RequestDTOs
│   │       └── ResponseDTOs
│   ├── config/
│   │   ├── LandRiskAiProperties
│   │   ├── GlobalExceptionHandler
│   │   └── CorsConfig
│   ├── entity/
│   │   ├── OrderEntity
│   │   ├── ReportEntity
│   │   ├── UserEntity
│   │   ├── ResellerEntity
│   │   ├── PaymentEntity
│   │   ├── NotificationEntity
│   │   └── AuditLogEntity
│   ├── service/
│   │   ├── OrderService
│   │   ├── ReportService
│   │   ├── PaymentService
│   │   ├── NotificationService
│   │   ├── UserService
│   │   ├── ResellerService
│   │   └── AuditService
│   ├── repo/
│   │   ├── OrderRepository
│   │   ├── ReportRepository
│   │   ├── UserRepository
│   │   ├── ResellerRepository
│   │   ├── PaymentRepository
│   │   ├── NotificationRepository
│   │   └── AuditLogRepository
│   ├── exception/
│   │   ├── LandRiskAiException
│   │   └── Exceptions (custom exceptions)
│   ├── util/
│   │   ├── EncryptionUtil
│   │   ├── ValidationUtil
│   │   └── Constants
│   ├── risk/
│   │   ├── RiskEngine
│   │   ├── RiskResult
│   │   └── RiskFinding
│   ├── pdf/
│   │   └── PdfReportService
│   ├── notify/
│   │   └── WhatsAppService
│   └── LandRiskAiApplication
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-prod.yml
│
├── build.gradle
└── Dockerfile
```

---

## Contributing

1. Fork repository
2. Create feature branch: `git checkout -b feature/your-feature`
3. Make changes and commit
4. Push and create Pull Request
5. Follow code review process

---

## Documentation Files

- **[ANALYSIS_AND_FIXES.md](ANALYSIS_AND_FIXES.md)** - Technical debt, identified issues, and fixes implemented
- **[build.gradle](build.gradle)** - Dependencies and build configuration
- **[application.yml](backend/src/main/resources/application.yml)** - Configuration reference

---

## Support

- **Issues**: Report via GitHub Issues
- **Questions**: Create Discussion
- **Security**: Report to security@landriskai.com

---

## License

Licensed under Apache 2.0. See LICENSE file for details.

---

**Ready to start?** Run `gradle -p backend bootRun` and visit http://localhost:8080/swagger-ui.html

