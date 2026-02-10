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
        // MVP: mock delivery only
        log.info("[MOCK_WHATSAPP] To: {} | {}", whatsappNumber, message);

        // Later:
        // - integrate WhatsApp Business Platform via BSP
        // - use message templates, status callbacks, retries
    }
}
