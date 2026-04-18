package com.madgarage.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double subtotal;
    private Double taxAmount;
    private Double shippingFee;
    private Double grandTotal;
    private String status;
    private LocalDateTime orderDate;

    // Shipping Details
    private String shippingAddress;
    private String city;
    private String state;
    private String pincode;

    // Fitting Details
    private String deliveryType; // HOME_DELIVERY or GARAGE_FITTING
    private Long fittingGarageId;
    private String fittingStatus; // NONE, PENDING_INSPECTION, INSPECTED, FITTED

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<OrderItem> items = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}