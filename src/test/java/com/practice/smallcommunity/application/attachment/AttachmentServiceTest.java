package com.practice.smallcommunity.application.attachment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.practice.smallcommunity.domain.attachment.UploadFile;
import com.practice.smallcommunity.domain.attachment.UploadFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    UploadFileRepository uploadFileRepository;

    AttachmentService attachmentService;

    @BeforeEach
    void setUp() {
        attachmentService = new AttachmentService(uploadFileRepository);
    }

    @Test
    void 파일을_업로드한다() {
        //given
        UploadFile uploadFile = spy(UploadFile.builder()
            .build());
        when(uploadFile.getId()).thenReturn(1L);

        //when
        Long result = attachmentService.upload(uploadFile);

        //then
        assertThat(result).isEqualTo(1L);
    }
}