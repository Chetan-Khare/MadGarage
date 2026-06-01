package com.madgarage.api.dto;

import com.madgarage.api.enums.ReturnReason;
import com.madgarage.api.enums.ReturnRequestType;
import com.madgarage.api.enums.ReturnStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReturnResponseDto {
    private Long id;
    private Long orderId;
    private Long userId;
    private String customerName;
    private ReturnReason reason;
    private ReturnRequestType requestType;
    private String description;
    private String imageUrls;
    private ReturnStatus status;
    private Long replacementOrderId;
    private String adminNote;
    private String refundId;
    private Double refundAmount;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
    private java.util.List<ReturnItemResponseDto> items;

    @Data
    public static class ReturnItemResponseDto {
        private Long orderItemId;
        private Integer quantity;
        private String partName;
    }
}
