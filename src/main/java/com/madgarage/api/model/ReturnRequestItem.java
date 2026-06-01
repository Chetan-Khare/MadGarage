package com.madgarage.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "return_request_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    @JsonIgnore
    private ReturnRequest returnRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    private Integer quantity;
}
