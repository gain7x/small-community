package com.practice.smallcommunity.interfaces.attachment;

import com.practice.smallcommunity.domain.attachment.FileStore;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Profile({"dev", "test"})
public class AttachmentDevController {

    private final FileStore fileStore;

    @GetMapping(value = "/images/{folderName}/{filename}", produces = {
        MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public Resource downloadLocalImage(@PathVariable String folderName, @PathVariable String filename)
        throws MalformedURLException {
        return new UrlResource(fileStore.getAccessUri(folderName + "/" + filename));
    }
}
