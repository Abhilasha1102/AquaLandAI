package com.landriskai.util;

/**
 * Application-wide constants
 */
public class Constants {

    // Business logic
    public static final Integer ORDER_AMOUNT_PAISE = 2500; // Rs 25
    public static final Integer DEFAULT_COMMISSION_PAISE = 500; // Rs 5 per report for reseller
    public static final Integer PAYMENT_TIMEOUT_MINUTES = 15;
    public static final Integer REPORT_LINK_TTL_DAYS = 7;
    public static final Integer DATA_RETENTION_DAYS = 90;
    public static final Integer RESELLER_APPROVAL_TTL_HOURS = 48;
    public static final Integer NOTIFICATION_MAX_RETRIES = 3;
    public static final Integer NOTIFICATION_RETRY_DELAY_SECONDS = 300; // 5 minutes

    // Verification & Security
    public static final Integer VERIFICATION_CODE_LENGTH = 32;
    public static final Integer TOKEN_LENGTH = 48;
    public static final Integer OTP_LENGTH = 6;
    public static final String REPORT_LINK_ALGORITHM = "HMAC-SHA256";

    // Risk scoring
    public static final Integer RISK_SCORE_MIN = 0;
    public static final Integer RISK_SCORE_MAX = 100;
    public static final Integer RISK_SCORE_BASE = 10;
    public static final Integer RISK_THRESHOLD_RED = 60;
    public static final Integer RISK_THRESHOLD_AMBER = 30;

    // Validation constraints
    public static final Integer KHATA_MAX_LENGTH = 50;
    public static final Integer KHESRA_MAX_LENGTH = 50;
    public static final Integer LOCATION_MAX_LENGTH = 100;
    public static final Integer LOCATION_MIN_LENGTH = 2;
    public static final Integer PHONE_MIN_LENGTH = 10;
    public static final Integer PHONE_MAX_LENGTH = 13;
    public static final Integer EMAIL_MAX_LENGTH = 255;

    // API Response messages
    public static final String ORDER_CREATED_SUCCESS = "Order created successfully";
    public static final String ORDER_PAID_SUCCESS = "Payment confirmed";
    public static final String REPORT_GENERATED_SUCCESS = "Report generated successfully";
    public static final String REPORT_DELIVERED_SUCCESS = "Report delivered successfully";
    public static final String INVALID_PHONE = "Invalid phone number format (10-13 digits required)";
    public static final String INVALID_KHATA = "Invalid Khata format";
    public static final String INVALID_KHESRA = "Invalid Khesra format";
    public static final String INVALID_LOCATION = "Invalid location format";

    // Admin audit
    public static final String AUDIT_RESEND_REPORT = "Manually resent report";
    public static final String AUDIT_MARK_REFUND = "Marked for refund";
    public static final String AUDIT_REGENERATE_REPORT = "Regenerated report";
    public static final String AUDIT_APPROVE_RESELLER = "Approved reseller application";
    public static final String AUDIT_REJECT_RESELLER = "Rejected reseller application";

    // Feature flags
    public static final Boolean ENABLE_WHATSAPP_DELIVERY = false; // Change to true in production
    public static final Boolean ENABLE_EMAIL_DELIVERY = false;
    public static final Boolean ENABLE_SMS_FALLBACK = false;

    // System
    public static final Integer MAX_CONCURRENT_REQUESTS = 100;
    public static final Integer REPORT_GENERATION_TIMEOUT_SECONDS = 180; // 3 minutes
}
