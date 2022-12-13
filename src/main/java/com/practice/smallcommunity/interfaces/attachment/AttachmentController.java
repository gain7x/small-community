package com.practice.smallcommunity.interfaces.attachment;

import com.practice.smallcommunity.application.attachment.AttachmentService;
import com.practice.smallcommunity.application.exception.BusinessException;
import com.practice.smallcommunity.application.exception.ErrorCode;
import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.attachment.FileStore;
import com.practice.smallcommunity.domain.attachment.StoredFile;
import com.practice.smallcommunity.domain.attachment.UploadFile;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.CurrentUser;
import com.practice.smallcommunity.interfaces.attachment.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AttachmentController {

    private final MemberService memberService;
    private final FileStore fileStore;
    private final AttachmentService attachmentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<UploadResponse> uploadImage(@CurrentUser Long loginId, MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }

        Member uploader = memberService.findByUserId(loginId);

        StoredFile storedFile = fileStore.storeFile(file);
        UploadFile uploadFile = UploadFile.builder()
            .uploader(uploader)
            .bucket(storedFile.getBucket())
            .objectKey(storedFile.getObjectKey())
            .url(storedFile.getUrl())
            .originalFilename(storedFile.getOriginalFilename())
            .fileSize(storedFile.getFileSize())
            .build();

        attachmentService.upload(uploadFile);

        log.info("File has been uploaded. id: {}, uploaderId: {}, bucket: {}, filename: {}, url: {}",
            uploadFile.getId(), uploadFile.getUploader().getId(),
            uploadFile.getBucket(), uploadFile.getOriginalFilename(), uploadFile.getUrl());

        return BaseResponse.Ok(UploadResponse.builder()
            .url(storedFile.getUrl())
            .build());
    }
}
