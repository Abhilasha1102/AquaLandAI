# 🚀 Quick Access Reference - AquaLandAI

## 📌 Important URLs

### User Interface
```
http://localhost:3000/index.html
```
✅ The beautiful 3-step form UI  
✅ Currently LIVE and running  
✅ Test cache reuse scenarios here

### Backend API Base
```
http://localhost:8081/api
```
⚙️ RESTful API endpoint  
✅ Cache checking  
✅ Order management  

### Cache Check Endpoint
```
GET http://localhost:8081/api/cache/check?khata=KH-12345&khesra=KH-SEC-001&district=Patna
```
🔍 Test this URL in browser or Postman  
✅ Returns 200 if cache found  
✅ Returns 404 if no cache  

---

## 🎮 Quick Commands

### Terminal 1: Start Backend
```bash
cd d:\VSProjects\AquaLandAI\landriskai-bihar\backend
.\gradlew.bat bootRun
```
⏱️ Takes ~6 seconds to start  
✅ Starts on port 8081  
✅ Watch for "Tomcat started on port 8081"

### Terminal 2: Start Frontend
```bash
cd d:\VSProjects\AquaLandAI\landriskai-bihar\frontend
npx http-server -p 3000
```
⏱️ Starts instantly  
✅ Starts on port 3000  
✅ Watch for "Available on: http://127.0.0.1:3000"

---

## 🧪 Testing Cache Scenarios

### Test 1: Fresh Search (No Cache)
```
1. Open: http://localhost:3000/index.html
2. Fill: District=Patna, Khata=KH-NEW-001, Khesra=KH-NEW-001
3. Expected: No cache badge, Price ₹25
```

### Test 2: Cached Search (80% Discount)
```
1. Same page (don't refresh)
2. Fill SAME land details: Khata=KH-NEW-001, Khesra=KH-NEW-001
3. Expected: Cache badge shown, Price ₹5, "[CACHED RESULT - 80% OFF]"
```

### Test 3: Different Land (New Cache)
```
1. Fill: Khata=KH-DIFF-002, Khesra=KH-DIFF-002
2. Expected: No cache badge, Price ₹25
```

---

## 🔗 API Testing with cURL

### Test Cache Hit
```bash
curl "http://localhost:8081/api/cache/check?khata=KH-12345&khesra=KH-SEC-001&district=Patna"
```

### Response (Cache Found):
```json
{
  "id": 1,
  "khata": "KH-12345",
  "khesra": "KH-SEC-001",
  "district": "Patna",
  "circle": "Patna City",
  "reusageCount": 2,
  "lastReuseAt": "2026-02-10T16:45:00Z",
  "expiresAt": "2026-02-17T10:00:00Z"
}
```

### Response (No Cache):
```
404 Not Found
```

---

## 📁 Important File Locations

### Frontend
```
d:\VSProjects\AquaLandAI\landriskai-bihar\frontend\
├─ index.html          ← Main UI file
├─ package.json        ← Dependencies
└─ favicon.ico         ← Icon
```

### Backend  
```
d:\VSProjects\AquaLandAI\landriskai-bihar\backend\
├─ src/main/java/com/landriskai/
│  ├─ api/
│  │  └─ OrderController.java        ← Cache endpoint here
│  ├─ repo/
│  │  └─ SearchCacheRepository.java  ← Database queries
│  ├─ entity/
│  │  └─ SearchCacheEntity.java      ← Cache table structure
│  └─ LandRiskAiApplication.java     ← Main app
├─ build.gradle                      ← Dependencies
└─ gradlew.bat                       ← Build tool
```

### Documentation
```
d:\VSProjects\AquaLandAI\landriskai-bihar\
├─ UI_AND_CACHING_GUIDE.md           ← Complete guide
├─ QUICK_START_UI.md                 ← Step-by-step testing
├─ SYSTEM_READY.md                   ← Current status
├─ SYSTEM_COMPLETE.md                ← Full summary
├─ VISUAL_UI_GUIDE.md                ← What you'll see
└─ QUICK_ACCESS_REFERENCE.md         ← This file
```

---

## 🔐 Database Information

### Database Type
- H2 (In-Memory for development)
- URL: `jdbc:h2:mem:` + auto-generated ID
- Auto-creates schema on startup

### Main Table: lr_search_cache
```sql
-- Check for cached records
SELECT * FROM lr_search_cache WHERE khata='KH-12345';

-- View all cache records
SELECT id, khata, khesra, district, reusageCount, expiresAt 
FROM lr_search_cache;

-- Total revenue from reuses
SELECT SUM(totalRevenueFromReusagePaise) FROM lr_search_cache;

-- Popular properties (most reused)
SELECT khata, reusageCount 
FROM lr_search_cache 
ORDER BY reusageCount DESC;

-- Expired cache (cleanup candidates)
SELECT * FROM lr_search_cache WHERE expiresAt < CURRENT_TIMESTAMP;
```

---

## 📊 Browser Developer Tools

### Check Frontend-Backend Communication
1. Open browser: http://localhost:3000/index.html
2. Press `F12` to open Developer Tools
3. Go to `Network` tab
4. Fill land details form
5. Watch API call to: `GET /api/cache/check?...`
6. See Response tab: 200 OK or 404 Not Found
7. Check Response body for cache data

### Check Browser Console
1. Press `F12`
2. Go to `Console` tab
3. Look for errors or warnings
4. Verify API calls are successful

---

## 🐛 Troubleshooting Checklist

### Issue: "Cannot reach localhost:3000"
- [ ] Is frontend running? Check Terminal 2
- [ ] Command should be: `npx http-server -p 3000`
- [ ] Should see: "Available on: http://127.0.0.1:3000"

### Issue: "Cannot reach localhost:8081"
- [ ] Is backend running? Check Terminal 1
- [ ] Command should be: `.\gradlew.bat bootRun`
- [ ] Should see: "Tomcat started on port 8081"

### Issue: Cache not detecting
- [ ] Wait 1 second after typing last field
- [ ] Check browser console (F12) for errors
- [ ] Verify all 3 fields filled (khata, khesra, district)
- [ ] Try different land values
- [ ] Refresh page and try again

### Issue: Price not changing from ₹25 to ₹5
- [ ] Check that cache was actually found (200 response in Network tab)
- [ ] Verify expiresAt is in future (not expired)
- [ ] Look for backend errors in Terminal 1
- [ ] Check browser console for JavaScript errors

### Issue: Backend shows error "Found 0 JPA repository interfaces"
- [ ] Rebuild: `.\gradlew.bat clean build -x test`
- [ ] Restart backend
- [ ] Should see "Found 8 JPA repository interfaces"

---

## 📈 Expected Test Results

### Test Outcome 1: New Search
```
✅ UI loads without errors
✅ Form fields are visible and editable
✅ No cache badge appears
✅ Step indicator shows 1️⃣ (ACTIVE)
✅ Click "Next: Payment" → Goes to Step 2
```

### Test Outcome 2: Cache Detection
```
✅ As you type khata/khesra → API call auto-triggers
✅ If first time: 404 response
✅ If repeat: 200 response with cache data
✅ Price updates: ₹25 → ₹5 (if cached)
✅ Badge shown: "[CACHED RESULT - 80% OFF]"
```

### Test Outcome 3: Payment Processing
```
✅ Step 2 form validates inputs
✅ WhatsApp field accepts 10-13 digits
✅ Email field validates email format
✅ Payment method options selectable
✅ Terms checkbox required
✅ Click "Proceed to Pay" → Goes to Step 3
```

### Test Outcome 4: Success Page
```
✅ Progress animation plays for 3 seconds
✅ Success page shows after animation
✅ Land location confirmed
✅ Khata/Khesra displayed
✅ Report validity: 7 Days
✅ "Start New Search" button visible
```

---

## 📱 Device Testing

### Desktop (1920×1080)
- ✅ Full 2-column form layout
- ✅ All elements visible
- ✅ Buttons full width at bottom
- ✅ No scrolling needed

### Tablet (768×1024)
- ✅ 1-column form layout
- ✅ Larger buttons for touch
- ✅ Stacked payment methods
- ✅ Responsive padding

### Mobile (375×812)
- ✅ Single column everything
- ✅ Touch-friendly targets (48px+)
- ✅ Full-width inputs
- ✅ Stacked buttons
- ✅ Readable text

---

## 🎯 Performance Benchmarks

### Expected Response Times
```
Cache Check: ~20ms (instant to user)
Price Update: ~26ms (perceived instant)
New Search: ~500ms (progress animation)
Success Display: ~3 seconds (animated)
Page Load: <1 second
Form Validation: <10ms
```

---

## 📞 Support Contact Points

### For Frontend Issues
1. Check browser console (F12 → Console)
2. Check Network tab for API calls
3. Verify localhost:3000 is running
4. Check `frontend/index.html` file

### For Backend Issues
1. Check Terminal 1 for startup messages
2. Look for "Found 8 JPA repository interfaces"
3. Check for "Tomcat started on port 8081"
4. Review Spring Boot log messages

### For Database Issues
1. Check backend logs for database connection
2. Look for H2 database initialization
3. Verify tables are created automatically
4. Check for Hibernate mapping errors

---

## 🚀 Production Deployment Checklist

Before going to production, ensure:
- [ ] Backend: Switch to PostgreSQL (not H2)
- [ ] Frontend: Replace localhost:8081 with production domain
- [ ] API: Add rate limiting
- [ ] Security: Enable HTTPS/SSL
- [ ] Auth: Implement user authentication
- [ ] Payment: Connect real payment gateway
- [ ] Notifications: Setup WhatsApp/Email delivery
- [ ] Database: Setup backups and migrations
- [ ] Monitoring: Enable logging and monitoring
- [ ] Testing: Run full integration tests
- [ ] Documentation: Update API docs
- [ ] Performance: Run load testing

---

## 📚 Quick Reference

| Component | Port | Status | Command |
|-----------|------|--------|---------|
| Frontend UI | 3000 | ✅ Running | `npx http-server -p 3000` |
| Backend API | 8081 | ✅ Running | `.\gradlew.bat bootRun` |
| Database | Auto | ✅ H2 | Auto-started with backend |
| Cache Table | - | ✅ Ready | `lr_search_cache` |

---

## 💡 Pro Tips

1. **Test Multiple Times**: Try the same search 3+ times to see cache increment
2. **Monitor Logs**: Check Terminal 1 for "Found X JPA repository interfaces"
3. **Use Network Tab**: See exact API calls and responses in F12
4. **Test Different Lands**: Try different khata/khesra combinations
5. **Check Database**: Backend logs show H2 database initialization
6. **Browser Refresh**: Clear cache with Ctrl+Shift+Del if needed
7. **Port Conflicts**: If ports in use, kill process or use different ports
8. **CORS Issues**: Frontend should communicate with backend without errors

---

## 🎉 Success Indicators

When everything is working correctly:
```
✅ Frontend UI loads beautifully
✅ No JavaScript errors in console
✅ API calls show 200/404 responses
✅ Price updates from ₹25 to ₹5
✅ Cache badge appears on reuse
✅ Progress animation plays smoothly
✅ Success page displays correctly
✅ Backend shows 8 repositories
✅ Database stores cache records
✅ No errors in terminal output
```

---

**Your AquaLandAI system is fully operational! 🚀**

