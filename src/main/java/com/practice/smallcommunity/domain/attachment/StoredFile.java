package com.practice.smallcommunity.domain.attachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StoredFile {

    private String bucket;
    private String objectKey;
    private String url;
    private String originalFilename;
    private long fileSize;
}
