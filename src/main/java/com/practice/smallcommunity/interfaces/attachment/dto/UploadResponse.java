package com.practice.smallcommunity.interfaces.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UploadResponse {

    private String url;
}
