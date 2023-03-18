package com.practice.smallcommunity.infrastructure.attachment;

import com.practice.smallcommunity.attachment.domain.FileStore;
import com.practice.smallcommunity.attachment.domain.StoredFile;
import java.io.File;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 애플리케이션이 실행 중인 시스템의 로컬 스토리지에 파일을 저장합니다.
 */
@Component
@Profile({"dev", "test"})
public class LocalFileStore implements FileStore {

    @Value("${dummy-store.attachments}")
    private String localBucket;

    @Override
    public StoredFile storeFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일 정보가 없습니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String folderName = UUID.randomUUID().toString();
        String objectKey = folderName + "/" + originalFilename;
        saveToBucket(multipartFile, objectKey);

        String url = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/" + objectKey)
            .toUriString();

        return StoredFile.builder()
            .bucket(localBucket)
            .objectKey(originalFilename)
            .originalFilename(originalFilename)
            .url(url)
            .fileSize(multipartFile.getSize())
            .build();
    }

    private void saveToBucket(MultipartFile multipartFile, String objectKey) {
        try {
            File folder = new File(localBucket + "/" + objectKey).getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            multipartFile.transferTo(new File(localBucket + "/" + objectKey));
        } catch (Exception e) {
            throw new RuntimeException("파일 저장을 실패했습니다.", e);
        }
    }

    @Override
    public String getAccessUri(String objectKey) {
        return "file:" + localBucket + "/" + objectKey;
    }
}
