package com.practice.smallcommunity.infrastructure;

import com.practice.smallcommunity.domain.attachment.FileStore;
import com.practice.smallcommunity.domain.attachment.StoredFile;
import java.io.File;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장소 클래스입니다.
 *  애플리케이션이 실행되는 시스템의 로컬 스토리지에 저장합니다.
 */
@Component
public class LocalFileStore implements FileStore {

    @Value("${server.api}")
    private String api;

    @Value("${file-store.dir}")
    private String fileDir;

    @Override
    public String getStoredPath(String bucket, String filename) {
        return fileDir + "/" + bucket + "/" + filename;
    }

    @Override
    public StoredFile storeFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일 정보가 없습니다.");
        }

        String bucket = UUID.randomUUID().toString();
        String originalFilename = multipartFile.getOriginalFilename();
        String bucketPath = getStoredPath(bucket, "");
        try {
            File bucketDir = new File(bucketPath);
            if (!bucketDir.exists()) {
                bucketDir.mkdirs();
            }
            multipartFile.transferTo(new File(getStoredPath(bucket, originalFilename)));
        } catch (Exception e) {
            throw new RuntimeException("파일 저장을 실패했습니다.", e);
        }

        return StoredFile.builder()
            .bucket(bucket)
            .originalFilename(originalFilename)
            .url(api + "/images/" + bucket + "/" + originalFilename)
            .build();
    }
}
