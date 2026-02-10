package com.landriskai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application configuration properties
 * Load from application.yml or environment variables
 */
@Data
@Component
@ConfigurationProperties(prefix = "landriskai")
public class LandRiskAiProperties {

    private Storage storage = new Storage();
    private Links links = new Links();
    private Security security = new Security();
    private Whatsapp whatsapp = new Whatsapp();
    private Payment payment = new Payment();
    private Email email = new Email();
    private Sms sms = new Sms();
    private Admin admin = new Admin();

    @Data
    public static class Storage {
        private String reportDir = "./data/reports";
        private String retentionDays = "90";
    }

    @Data
    public static class Links {
        private String baseUrl = "http://localhost:8080";
        private String reportLinkPath = "/api/reports/{reportId}/download";
        private String verifyLinkPath = "/api/reports/{reportId}/verify";
    }

    @Data
    public static class Security {
        private int reportLinkTtlDays = 7;
        private String encryptionKey = ""; // Load from env or vault
        private boolean corsEnabled = true;
        private String corsAllowedOrigins = "http://localhost:3000";
        private int maxRequestsPerMinute = 100;
    }

    @Data
    public static class Whatsapp {
        private boolean enabled = false;
        private String apiKey = ""; // Load from env
        private String apiSecret = ""; // Load from env
        private String businessAccountId = "";
        private String phoneNumberId = "";
        private int maxRetries = 3;
        private int retryDelaySeconds = 300;
    }

    @Data
    public static class Payment {
        private String gateway = "RAZORPAY"; // RAZORPAY, PAYTM, CASHFREE
        private String apiKey = ""; // Load from env
        private String apiSecret = ""; // Load from env
        private int timeoutMinutes = 15;
        private int pollIntervalSeconds = 30;
        private int reconciliationRetries = 5;
    }

    @Data
    public static class Email {
        private boolean enabled = false;
        private String provider = "SMTP"; // SMTP, SENDGRID, AWS_SES
        private String smtpHost = "";
        private int smtpPort = 587;
        private String smtpUsername = ""; // Load from env
        private String smtpPassword = ""; // Load from env
        private String fromAddress = "noreply@landriskai.com";
    }

    @Data
    public static class Sms {
        private boolean enabled = false;
        private String provider = "TWILIO"; // TWILIO, EXOTEL, FAST2SMS
        private String apiKey = ""; // Load from env
        private String apiSecret = ""; // Load from env
    }

    @Data
    public static class Admin {
        private int sessionTimeoutMinutes = 30;
        private boolean auditLoggingEnabled = true;
        private int auditRetentionDays = 365;
    }
}
