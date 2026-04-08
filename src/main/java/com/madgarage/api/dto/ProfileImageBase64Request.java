package com.madgarage.api.dto;

import lombok.Data;

@Data
public class ProfileImageBase64Request {
    private String base64Image;
    private String extension;
}
