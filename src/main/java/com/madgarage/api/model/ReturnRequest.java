package com.madgarage.api.model;

import com.madgarage.api.enums.ReturnReason;
import com.madgarage.api.enums.ReturnRequestType;
import com.madgarage.api.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ReturnReason reason;

    @Enumerated(EnumType.STRING)
    private ReturnRequestType requestType;

    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String imageUrls; // Comma-separated URLs

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    private Long replacementOrderId; // Reference to the new order if replacement

    private String adminNote;

    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
}
