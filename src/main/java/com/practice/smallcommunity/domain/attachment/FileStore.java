package com.practice.smallcommunity.domain.attachment;

import org.springframework.web.multipart.MultipartFile;

public interface FileStore {

    /**
     * 파일을 저장하고, 저장된 파일 정보를 반환합니다.
     * @param multipartFile 파일
     * @return 저장 파일 정보
     * @throws IllegalArgumentException
     *          유효하지 않은 파일 정보
     * @throws RuntimeException
     *          파일 저장 실패, ...
     */
    StoredFile storeFile(MultipartFile multipartFile);

    /**
     * 저장된 파일에 접근 가능한 URI를 반환합니다.
     * @param objectKey 버킷에서 파일을 가리키는 키
     * @return 저장된 파일에 접근 가능한 URI
     */
    String getAccessUri(String objectKey);
}
