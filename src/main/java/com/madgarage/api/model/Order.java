package com.madgarage.api.model;

import com.madgarage.api.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_order_date", columnList = "orderDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Order {
    
    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double subtotal;
    private Double taxAmount;
    private Double shippingFee;
    private Double platformFee;
    private Double grandTotal;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime deliveredAt;

    // Coupon Details
    private String appliedCouponCode;
    private Double discountAmount;

    // Shipping Details
    private String shippingAddress;
    private String city;
    private String state;
    private String pincode;

    // Fitting Details
    private String deliveryType; // HOME_DELIVERY or GARAGE_FITTING
    private Long fittingGarageId;
    private String fittingStatus; // NONE, PENDING_INSPECTION, INSPECTED, FITTED

    // Payment Verification Terminal
    private String razorpayOrderId;
    private String paymentId;
    private String paymentSignature;
    private boolean paymentVerified;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<OrderItem> items = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}