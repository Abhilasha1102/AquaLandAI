# Quick Start Guide - AquaLandAI Multi-Step UI

## 🎬 Launch the System

### Terminal 1: Start Backend (Spring Boot)
```bash
cd d:\VSProjects\AquaLandAI\landriskai-bihar\backend
.\gradlew.bat bootRun
```
✅ Server starts on: **http://localhost:8081**

### Terminal 2: Start Frontend (HTTP Server)
```bash
cd d:\VSProjects\AquaLandAI\landriskai-bihar\frontend
npx http-server -p 3000
```
✅ UI runs on: **http://localhost:3000**

---

## 🎯 Using the User Interface

### Step 1️⃣ - Enter Land Details
**URL**: http://localhost:3000/index.html

```
Fill in these fields:
┌─────────────────────────────────────┐
│ District *              (required)    │
│ Circle/Block *          (required)    │
│ Village/Mauza *         (required)    │
│ Owner Name              (optional)    │
│ Khata Number *          (required)    │
│ Khesra Number *         (required)    │
│ Plot Area               (optional)    │
└─────────────────────────────────────┘
```

**Example Data**:
```
District: Patna
Circle: Patna City
Village: Ram Nagar
Owner Name: Ram Kumar
Khata Number: KH-12345
Khesra Number: KH-SEC-001
Plot Area: 2500 sq ft
```

**What Happens**:
- As you type khata/khesra/district, system checks cache
- If land was searched before: Shows cache info badge
- If cached: Price automatically updates to ₹5 (was ₹25)

**Visual Feedback**:
```
WITHOUT CACHE:
┌──────────────────────────┐
│ Land Assessment          │
│ ₹25.00                   │
└──────────────────────────┘

WITH CACHE (80% discount):
┌──────────────────────────┐
│ CACHED RESULT - 80% OFF  │ ← Badge
│ Land Assessment ₹25 → ₹5 │ ← Price reduction
│ You saved ₹20!           │ ← Savings shown
└──────────────────────────┘
```

---

### Step 2️⃣ - Payment & Delivery Info
**Click**: "Next: Payment →"

```
Price Section (Auto-updates):
┌─────────────────────────────────────┐
│ Land Assessment          ₹25.00      │
│ Total Amount             ₹25.00      │
│                    (or ₹5.00 cached) │
└─────────────────────────────────────┘

Contact Information:
┌─────────────────────────────────────┐
│ WhatsApp Number *       +91 |_______|│
│                                      │
│ Email Address *         |____________|│
└─────────────────────────────────────┘

Payment Method (Choose one):
┌──────────────────────────────────────┐
│ ○ 💳 Razorpay    ● 📱 Paytm    ○ 🏦 Cashfree │
└──────────────────────────────────────┘

☑ I agree to Terms of Service & Privacy Policy
```

**Example Data**:
```
WhatsApp: 9876543210
Email: user@example.com
Payment: Razorpay
Agreement: Checked ✓
```

**What Happens**:
- Form validates WhatsApp format (10-13 digits)
- Form validates email format
- Checks terms agreement checkbox
- Ready for payment processing

---

### Step 3️⃣ - Processing & Results
**Click**: "Proceed to Pay →"

**Progress Animation**:
```
Processing your land assessment...

████░░░░░ 40%

✓ Validating Input
  → Running Risk Analysis
  → Generating Report
  → Sending Notifications
```

**After 3 seconds - Success Display**:
```
✅ Report Generated Successfully!

Your land assessment report has been generated 
and will be sent to your WhatsApp and Email.

┌─────────────────────────────────────┐
│ Land Location:   Ram Nagar, Patna   │
│ Khata/Khesra:    KH-12345/KH-SEC-001│
│ Report Validity: 7 Days             │
│ Report Type:     New/Cached Report  │
└─────────────────────────────────────┘

[Start New Search Button]
```

---

## 🔄 Testing Cache & Discounts

### Scenario A: First-Time User (No Cache)

**Timeline**: 
- First search ever for this land

**Expected Flow**:
1. Enter land details
2. Step 1 → No cache info badge
3. Step 2 → Price shows ₹25
4. Complete payment
5. See result

**Backend Log**:
```
GET /api/cache/check?khata=KH-12345&khesra=KH-SEC-001&district=Patna
→ 404 Not Found (no cache)
→ Create new SearchCacheEntity
→ Generate report
→ Store in cache with expiresAt = now() + 7 days
```

---

### Scenario B: Reuse Within 7 Days (WITH Cache & Discount)

**Timeline**:
- Same user searches again within 7 days
- OR Different user searches same land

**Expected Flow**:
1. Enter same land details
2. Step 1 → Shows "Quick reuse available - 80% discount!" badge
3. Step 1 → Auto-checks cache on khata/khesra/district change
4. Step 2 → Price automatically updates to ₹5
5. Step 2 → Badge shows "CACHED RESULT - 80% OFF"
6. Step 2 → "You saved ₹20!" message
7. Complete payment (paying only ₹5)
8. See result (cached report delivered)

**Backend Log**:
```
GET /api/cache/check?khata=KH-12345&khesra=KH-SEC-001&district=Patna
→ 200 OK (cache found)
→ Increment reusageCount (1 → 2)
→ Add 500 paise to totalRevenueFromReusagePaise
→ Update lastReuseAt = now()
→ Return cached data
→ Frontend receives cache info
→ Frontend auto-adjusts price: 2500 → 500 paise
```

---

### Scenario C: After 7 Days (Cache Expired)

**Timeline**:
- User searches same land after 7 days

**Expected Flow**:
1. Enter same land details  
2. Step 1 → No cache info (cache expired)
3. Step 1 → Price remains ₹25
4. Steps 2 & 3 → Full price, new report generated
5. New cache entry created with new expiresAt

**Backend Log**:
```
GET /api/cache/check?khata=KH-12345&...
→ 404 Not Found (cache expired)
→ Check: expiresAt > current_timestamp? NO
→ Create NEW SearchCacheEntity with new expiresAt
```

---

## 📊 Visual Step Indicator

At the top of the screen, you'll see step progress:

```
Progress Indicator (Step 1 - Active):
    1️⃣         2         3
  [ACTIVE]   [PENDING] [PENDING]
    Land      Payment   Results
   Details

Progress Indicator (Step 2 - Active):
    ✓         2️⃣        3
  [DONE]    [ACTIVE]  [PENDING]
    Land      Payment   Results
   Details

Progress Indicator (Step 3 - Active):
    ✓         ✓         3️⃣
  [DONE]    [DONE]    [ACTIVE]
    Land      Payment   Results
   Details
```

---

## 🗂️ How Caching Works Behind Scenes

### Database Schema
```sql
CREATE TABLE lr_search_cache (
    id BIGINT PRIMARY KEY,
    khata VARCHAR(50) NOT NULL,
    khesra VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    circle VARCHAR(50) NOT NULL,
    village VARCHAR(100) NOT NULL,
    searchHash VARCHAR(64) UNIQUE NOT NULL,
    
    -- Cached results
    riskAnalysisJson TEXT,
    findingsJson TEXT,
    riskBand VARCHAR(20),
    riskScore INT,
    pdfPath VARCHAR(500),
    
    -- Cache TTL & tracking
    expiresAt TIMESTAMP NOT NULL,
    reusageCount INT DEFAULT 0,
    lastReuseAt TIMESTAMP,
    totalRevenueFromReusagePaise BIGINT DEFAULT 0,
    
    createdAt TIMESTAMP,
    updatedAt TIMESTAMP,
    
    INDEX idx_khata_khesra (khata, khesra),
    INDEX idx_location (district, circle, village),
    INDEX idx_expires_at (expiresAt),
    INDEX idx_created_at (createdAt)
);
```

### Query Logic
```java
// When user fills land details, this query runs:
SELECT * FROM lr_search_cache 
WHERE khata = 'KH-12345' 
  AND khesra = 'KH-SEC-001' 
  AND district = 'Patna'
  AND expiresAt > CURRENT_TIMESTAMP
LIMIT 1;

// If found: Return cached report + update counters
// If not found: Return 404 + proceed with new search
```

---

## 💰 Pricing Breakdown

| Scenario | Price | Revenue | Total Reuses |
|----------|-------|---------|--------------|
| New search | ₹25 | ₹25 | 1 |
| 1st reuse (day 2) | ₹5 | ₹5 | 2 |
| 2nd reuse (day 4) | ₹5 | ₹5 | 3 |
| 3rd reuse (day 6) | ₹5 | ₹5 | 4 |
| Total for 1 land | - | **₹40** | 4 |

**Without Caching**: Would charge ₹25 × 4 = ₹100 (but user wouldn't reuse)  
**With Caching**: Charges ₹25 + ₹15 = **₹40** (reusable, captures volume)

---

## 🔧 Troubleshooting

### Issue: "Cannot reach http://localhost:3000"
**Solution**:
```bash
# Check if HTTP server is running
cd frontend && npx http-server -p 3000
```

### Issue: "Cannot reach http://localhost:8081/api"
**Solution**:
```bash
# Check if backend is running
cd backend && .\gradlew.bat bootRun
# Should see: "Tomcat started on port 8081"
```

### Issue: Cache info not showing up
**Solution**:
- Ensure khata, khesra, AND district are filled
- Wait 1 second for automatic check
- Open browser console (F12) to see API call
- Verify backend logs show: "GET /api/cache/check"

### Issue: Price doesn't update to ₹5
**Solution**:
- Check cache was actually found (no 404 in logs)
- Verify expiresAt timestamp is in future
- Check reusageCount incremented in database

---

## 📱 Mobile Responsiveness

UI works on:
- ✅ Desktop (1920×1080)
- ✅ Tablet (768×1024)  
- ✅ Mobile (375×812)

Changes automatically:
- Single column layout on mobile
- Larger tap targets (20px+)
- Full-width inputs
- Stacked buttons

---

## 🚀 What's Next

1. **Try the workflow** with sample data
2. **Generate 2 reports** with same land to test cache
3. **Check database** to see SearchCacheEntity records
4. **Monitor logs** to see cache hits/misses
5. **Report issues** found during testing

---

## 📞 Support

**Endpoints**:
- Frontend: http://localhost:3000
- Backend: http://localhost:8081
- Cache Check: http://localhost:8081/api/cache/check
- API Docs: http://localhost:8081/swagger-ui.html (when enabled)

