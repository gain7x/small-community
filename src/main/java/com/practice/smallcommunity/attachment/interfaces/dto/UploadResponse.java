package com.practice.smallcommunity.attachment.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UploadResponse {

    private String url;
}
