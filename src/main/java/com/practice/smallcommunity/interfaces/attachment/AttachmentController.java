package com.practice.smallcommunity.interfaces.attachment;

import com.practice.smallcommunity.application.AttachmentService;
import com.practice.smallcommunity.application.MemberService;
import com.practice.smallcommunity.domain.attachment.FileStore;
import com.practice.smallcommunity.domain.attachment.StoredFile;
import com.practice.smallcommunity.domain.attachment.UploadFile;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.CurrentUser;
import com.practice.smallcommunity.interfaces.attachment.dto.UploadResponse;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AttachmentController {

    private final MemberService memberService;
    private final FileStore fileStore;
    private final AttachmentService attachmentService;

    @GetMapping(value = "/images/{bucket}/{filename}", produces = {
        MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public Resource downloadLocalImage(@PathVariable String bucket, @PathVariable String filename)
        throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getStoredPath(bucket, filename));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<UploadResponse> uploadImage(@CurrentUser Long loginId, MultipartFile file) {
        Member uploader = memberService.findByUserId(loginId);
        StoredFile storedFile = fileStore.storeFile(file);
        UploadFile uploadFile = UploadFile.builder()
            .uploader(uploader)
            .bucket(storedFile.getBucket())
            .filename(storedFile.getOriginalFilename())
            .url(storedFile.getUrl())
            .build();

        attachmentService.upload(uploadFile);

        return BaseResponse.Ok(UploadResponse.builder()
            .url(storedFile.getUrl())
            .build());
    }
}
