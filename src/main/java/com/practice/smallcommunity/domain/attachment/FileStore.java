package com.practice.smallcommunity.domain.attachment;

import org.springframework.web.multipart.MultipartFile;

public interface FileStore {

    /**
     * 저장된 파일명을 기준으로 저장된 전체 경로를 반환합니다.
     * @param bucket 파일이 저장된 버킷
     * @param originalFilename 파일명
     * @return ( 디렉토리 경로 등을 포함하는 )전체 경로
     */
    String getStoredPath(String bucket, String originalFilename);

    /**
     * 파일을 저장하고, 저장된 파일 정보를 반환합니다.
     * @param multipartFile 멀티파트 파일
     * @return 저장 파일 정보
     * @throws IllegalArgumentException
     *          유효하지 않은 파일 정보
     * @throws RuntimeException
     *          파일 저장 실패, ...
     */
    StoredFile storeFile(MultipartFile multipartFile);
}
