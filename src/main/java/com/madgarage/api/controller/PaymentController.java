package com.madgarage.api.controller;

import com.madgarage.api.services.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;
    private final com.madgarage.api.services.OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(Principal principal, @RequestBody Map<String, Object> data) {
        try {
            Long orderId = Long.parseLong(data.get("orderId").toString());
            com.madgarage.api.model.Order order = orderService.getOrderById(orderId);
            
            if (principal == null || order.getUser() == null || !order.getUser().getEmail().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }

            String razorpayOrderId = razorpayService.createOrder(order.getGrandTotal(), "rcpt_" + orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("razorpay_order_id", razorpayOrderId);
            response.put("amount", order.getGrandTotal());
            response.put("currency", "INR");
            
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
