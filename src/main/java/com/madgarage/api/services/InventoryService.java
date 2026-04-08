package com.madgarage.api.services;

import com.madgarage.api.dto.ProductResponse;
import com.madgarage.api.dto.SellerAnalyticsResponse;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.OrderRepository;
import com.madgarage.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public SellerAnalyticsResponse getSellerAnalytics(User seller) {
        long activeListings = productRepository.countBySeller(seller);
        Double totalRevenue = orderRepository.calculateRevenueBySeller(seller);
        return new SellerAnalyticsResponse(activeListings, totalRevenue != null ? totalRevenue : 0.0);
    }

    public List<ProductResponse> getSellerProducts(User seller) {
        return productRepository.findBySeller(seller).stream()
                .map(productService::mapToResponse)
                .collect(Collectors.toList());
    }
}
