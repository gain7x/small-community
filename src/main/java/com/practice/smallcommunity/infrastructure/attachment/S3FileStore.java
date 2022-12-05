package com.practice.smallcommunity.infrastructure.attachment;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.practice.smallcommunity.domain.attachment.FileStore;
import com.practice.smallcommunity.domain.attachment.StoredFile;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Amazon S3에 파일을 저장합니다.
 */
@RequiredArgsConstructor
@Component
@Profile("prod")
public class S3FileStore implements FileStore {

    private static final String PREFIX_OBJECT_FOLDER = "images/";

    @Value("${cloud.aws.s3.bucket.attachments}")
    private String bucketName;

    @Value("${cloud.aws.cloudFront.attachments.cname}")
    private String cloudFront;

    private final AmazonS3 s3;

    @Override
    public StoredFile storeFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일 정보가 없습니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String folderName = UUID.randomUUID().toString();
        String objectKey = PREFIX_OBJECT_FOLDER + folderName + "/" + originalFilename;
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());

            s3.putObject(bucketName, objectKey, multipartFile.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장을 실패했습니다.", e);
        }

        return StoredFile.builder()
            .bucket(bucketName)
            .objectKey(objectKey)
            .url(getAccessUri(objectKey))
            .originalFilename(originalFilename)
            .fileSize(multipartFile.getSize())
            .build();
    }

    @Override
    public String getAccessUri(String objectKey) {
        return cloudFront + "/" + objectKey;
    }
}
