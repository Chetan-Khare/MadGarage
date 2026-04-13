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

}
