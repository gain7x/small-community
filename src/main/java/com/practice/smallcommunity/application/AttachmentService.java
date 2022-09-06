package com.practice.smallcommunity.application;

import com.practice.smallcommunity.domain.attachment.UploadFile;
import com.practice.smallcommunity.domain.attachment.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AttachmentService {

    private final UploadFileRepository uploadFileRepository;

    public Long upload(UploadFile uploadFile) {
        uploadFileRepository.save(uploadFile);
        return uploadFile.getId();
    }
}
