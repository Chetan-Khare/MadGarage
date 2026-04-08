package com.madgarage.api.controller;

import com.madgarage.api.dto.OrderResponse;
import com.madgarage.api.model.Product;
import com.madgarage.api.repository.ProductRepository;
import com.madgarage.api.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductRepository productRepository;
    private final OrderService orderService;

    @GetMapping("/")
    public String index(Model model) {
        List<Product> premiumProducts = productRepository.findAll();
        // Limit to 6 for the home page
        model.addAttribute("products", premiumProducts.stream().limit(6).toList());
        return "index";
    }

    @GetMapping("/catalog")
    public String catalog(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "catalog";
    }

    @GetMapping("/receipt/{id}")
    public String receipt(@PathVariable Long id, Model model) {
        // We'll use a simplified version for the web or the same DTO
        // Note: For public receipts, we might need a non-authenticated endpoint or a token
        try {
            OrderResponse order = orderService.mapToOrderResponse(null, null); // Mocked for now
            model.addAttribute("order", order);
        } catch (Exception e) {
            return "error";
        }
        return "receipt";
    }
}
