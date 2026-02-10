package com.landriskai.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyReportRequest {
    @NotBlank(message = "Verification code is required")
    private String verificationCode;
    
    private String otp;
}
