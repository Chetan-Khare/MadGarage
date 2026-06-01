package com.madgarage.api.controller;

import com.madgarage.api.enums.ReturnStatus;
import com.madgarage.api.model.ReturnRequest;
import com.madgarage.api.repository.ReturnRepository;
import com.madgarage.api.services.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks/razorpay")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayWebhookController.class);

    private final RazorpayService razorpayService;
    private final ReturnRepository returnRepository;

    @PostMapping
    public ResponseEntity<?> handleWebhook(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String signature) {
        boolean isValid = razorpayService.verifyWebhookSignature(payload, signature);
        if (!isValid) {
            logger.warn("Invalid Razorpay webhook signature detected.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            JSONObject json = new JSONObject(payload);
            String event = json.optString("event");

            if ("refund.processed".equals(event) || "refund.failed".equals(event)) {
                JSONObject refundObj = json.getJSONObject("payload").getJSONObject("refund").getJSONObject("entity");
                String refundId = refundObj.getString("id");

                Optional<ReturnRequest> optRequest = returnRepository.findByRefundId(refundId);
                if (optRequest.isPresent()) {
                    ReturnRequest request = optRequest.get();
                    if ("refund.processed".equals(event)) {
                        request.setStatus(ReturnStatus.REFUNDED);
                        logger.info("Webhook: Refund {} processed for ReturnRequest {}", refundId, request.getId());
                    } else if ("refund.failed".equals(event)) {
                        request.setStatus(ReturnStatus.REFUND_FAILED);
                        logger.warn("Webhook: Refund {} failed for ReturnRequest {}", refundId, request.getId());
                    }
                    returnRepository.save(request);
                } else {
                    logger.warn("Webhook: Received refund event for unknown refund ID: {}", refundId);
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error processing Razorpay webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
