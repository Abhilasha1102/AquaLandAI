package com.landriskai.notify;

import com.landriskai.config.LandRiskAiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WhatsAppService {

    private final LandRiskAiProperties props;

    public WhatsAppService(LandRiskAiProperties props) {
        this.props = props;
    }

    public void sendReportLink(String whatsappNumber, String message) {
        if (whatsappNumber == null || whatsappNumber.isBlank()) {
            log.warn("[WHATSAPP] Skipping send: missing recipient number");
            return;
        }

        if (message == null || message.isBlank()) {
            log.warn("[WHATSAPP] Skipping send to {}: empty message", whatsappNumber);
            return;
        }

        if (!props.getWhatsapp().isEnabled()) {
            log.info("[MOCK_WHATSAPP] To: {} | Message: {}", whatsappNumber, message);
            return;
        }

        // TODO: Integrate with a real provider (Twilio/Meta Cloud API/etc.)
        try {
            log.info("[WHATSAPP] Sending to: {}", whatsappNumber);
            // API call would go here
            log.info("[WHATSAPP] Message sent successfully");
        } catch (Exception e) {
            log.error("[WHATSAPP] Failed to send to {}", whatsappNumber, e);
        }
    }
}
