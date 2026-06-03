package com.madgarage.api.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Service
public class RazorpayService {

    @Value("${app.razorpay.key-id}")
    private String keyId;

    @Value("${app.razorpay.key-secret}")
    private String keySecret;

    @Value("${app.razorpay.webhook-secret:default-secret}")
    private String webhookSecret;

    private RazorpayClient client;

    @PostConstruct
    public void init() throws RazorpayException {
        if (keyId != null && !keyId.contains("placeholder") && !keyId.trim().startsWith("$") && !keyId.isEmpty()) {
            keyId = keyId.trim();
            keySecret = keySecret.trim();
            this.client = new RazorpayClient(keyId, keySecret);
        }
    }

    public String createOrder(double amount, String receipt) throws RazorpayException {
        if (client == null) {
            return "order_mock_" + System.currentTimeMillis();
        }

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100)); // amount in the smallest currency unit (paise)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);

        Order order = client.orders.create(orderRequest);
        return order.get("id");
    }

    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        boolean isMockEnv = keyId == null || keyId.trim().startsWith("$") || keyId.trim().startsWith("rzp_test_");
        if (isMockEnv) return true;
        if (razorpaySignature != null && razorpaySignature.startsWith("mock_signature_")) return false;

        if (client == null) {
            return true; // Gracefully allow mocks if client is absent
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    public com.razorpay.Refund refundPayment(String paymentId, double amount, String receipt) throws RazorpayException {
        // Always mock refund for test keys to avoid "payment not captured" errors during local testing
        if (client == null || keyId == null || keyId.trim().startsWith("rzp_test_") || keyId.trim().startsWith("$")) {
            return new com.razorpay.Refund(new org.json.JSONObject().put("id", "rfnd_mock_" + System.currentTimeMillis()));
        }

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", (int)(amount * 100)); // amount in paise
        refundRequest.put("receipt", receipt);

        return client.payments.refund(paymentId, refundRequest);
    }
}
