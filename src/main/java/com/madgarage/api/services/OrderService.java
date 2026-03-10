package com.madgarage.api.services;

import com.madgarage.api.dto.OrderRequest;
import com.madgarage.api.model.*;
import com.madgarage.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional // If anything fails, it rolls back the whole database transaction
    public Order placeOrder(User customer, OrderRequest request) {
        Order order = Order.builder()
                .user(customer)
                .totalAmount(request.getTotalAmount())
                .status("PROCESSING")
                .orderDate(LocalDateTime.now())
                .build();

        for (OrderRequest.CartItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Not enough stock for: " + product.getPartName());
            }

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .priceAtPurchase(itemDto.getPrice())
                    .build();

            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
    }
}
