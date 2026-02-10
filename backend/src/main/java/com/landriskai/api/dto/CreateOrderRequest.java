package com.landriskai.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enhanced CreateOrderRequest with comprehensive validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "District is required")
    @Size(min = 2, max = 100, message = "District must be 2-100 characters")
    private String district;

    @NotBlank(message = "Circle/Block is required")
    @Size(min = 2, max = 100, message = "Circle must be 2-100 characters")
    private String circle;

    @NotBlank(message = "Village/Mauza is required")
    @Size(min = 2, max = 100, message = "Village must be 2-100 characters")
    private String village;

    @NotBlank(message = "Khata number is required")
    @Size(min = 1, max = 50, message = "Khata must be 1-50 characters")
    private String khata;

    @NotBlank(message = "Khesra number is required")
    @Size(min = 1, max = 50, message = "Khesra must be 1-50 characters")
    private String khesra;

    @Size(max = 255, message = "Owner name must be max 255 characters")
    private String ownerName;

    @Size(max = 50, message = "Plot area must be max 50 characters")
    private String plotArea;

    @NotBlank(message = "WhatsApp number is required")
    @Pattern(regexp = "^[0-9]{10,13}$", message = "WhatsApp number must be 10-13 digits")
    private String whatsappNumber;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be max 255 characters")
    private String emailAddress;

    @Size(max = 50, message = "Reseller code must be max 50 characters")
    private String resellerCode;
}

