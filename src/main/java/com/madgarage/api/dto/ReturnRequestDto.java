package com.madgarage.api.dto;

import com.madgarage.api.enums.ReturnReason;
import com.madgarage.api.enums.ReturnRequestType;
import lombok.Data;
import java.util.List;

@Data
public class ReturnRequestDto {
    @jakarta.validation.constraints.NotNull(message = "Order ID is mandatory")
    private Long orderId;

    @jakarta.validation.constraints.NotNull(message = "Reason is mandatory")
    private ReturnReason reason;

    @jakarta.validation.constraints.NotNull(message = "Request type is mandatory")
    private ReturnRequestType requestType;

    @jakarta.validation.constraints.NotBlank(message = "Description is required")
    private String description;

    private List<String> imageUrls;

    private List<ReturnItemDto> items;

    @Data
    public static class ReturnItemDto {
        private Long orderItemId;
        private Integer quantity;
    }
}
