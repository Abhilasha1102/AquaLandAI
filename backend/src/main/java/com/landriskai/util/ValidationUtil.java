package com.landriskai.util;

import java.util.regex.Pattern;

/**
 * Validation utilities for input sanitization and validation (preventing injection attacks)
 */
public class ValidationUtil {

    // Pattern validators
    private static final Pattern KHATA_PATTERN = Pattern.compile("^[0-9A-Z\\-/]+$");
    private static final Pattern KHESRA_PATTERN = Pattern.compile("^[0-9A-Z\\-/]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,13}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-']{2,100}$");

    /**
     * Validate Khata format (alphanumeric, hyphens, slashes allowed)
     */
    public static boolean isValidKhata(String khata) {
        if (khata == null || khata.trim().isEmpty()) return false;
        if (khata.length() > 50) return false;
        return KHATA_PATTERN.matcher(khata.toUpperCase()).matches();
    }

    /**
     * Validate Khesra format
     */
    public static boolean isValidKhesra(String khesra) {
        if (khesra == null || khesra.trim().isEmpty()) return false;
        if (khesra.length() > 50) return false;
        return KHESRA_PATTERN.matcher(khesra.toUpperCase()).matches();
    }

    /**
     * Validate phone number (10-13 digits, no special characters)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        if (email.length() > 255) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate location names (district, circle, village, mauza)
     */
    public static boolean isValidLocation(String location) {
        if (location == null || location.trim().isEmpty()) return false;
        if (location.length() > 100) return false;
        // Allow letters, numbers, spaces, hyphens, apostrophes (for names like "O'Neill")
        return LOCATION_PATTERN.matcher(location).matches();
    }

    /**
     * Validate plot area format (numeric with optional unit)
     */
    public static boolean isValidPlotArea(String area) {
        if (area == null || area.trim().isEmpty()) return false;
        if (area.length() > 50) return false;
        // Allow numbers, decimal point, and common units: sqft, sqm, acre, bigha, etc.
        return area.matches("^[0-9]+(\\.?[0-9]+)?\\s*(sqft|sqm|sq\\.ft|sq\\.m|acre|bigha)?$");
    }

    /**
     * Sanitize string input (remove potentially dangerous characters)
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        // Remove null bytes and control characters
        return input.replaceAll("\\x00", "")
                   .replaceAll("[\\p{Cc}\\p{Cs}]", "");
    }

    /**
     * Validate report rating (1-5)
     */
    public static boolean isValidRating(Integer rating) {
        return rating != null && rating >= 1 && rating <= 5;
    }

    /**
     * Sanitize feedback text (max 1000 chars, no SQL injection)
     */
    public static String sanitizeFeedback(String feedback) {
        if (feedback == null) return null;
        String sanitized = sanitize(feedback);
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
        }
        return sanitized;
    }
}
