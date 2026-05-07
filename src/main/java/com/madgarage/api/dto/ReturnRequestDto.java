package com.madgarage.api.dto;

import com.madgarage.api.enums.ReturnReason;
import com.madgarage.api.enums.ReturnRequestType;
import lombok.Data;
import java.util.List;

@Data
public class ReturnRequestDto {
    private Long orderId;
    private ReturnReason reason;
    private ReturnRequestType requestType;
    private String description;
    private List<String> imageUrls;
}
