package com.madgarage.api.controller;

import com.madgarage.api.services.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;
    private final com.madgarage.api.services.OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            Long orderId = Long.parseLong(data.get("orderId").toString());
            com.madgarage.api.model.Order order = orderService.getOrderById(orderId);
            
            if (order == null) return ResponseEntity.notFound().build();

            String razorpayOrderId = razorpayService.createOrder(order.getGrandTotal(), "rcpt_" + orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("razorpay_order_id", razorpayOrderId);
            response.put("amount", order.getGrandTotal());
            response.put("currency", "INR");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
