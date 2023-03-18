package com.practice.smallcommunity.attachment.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.practice.smallcommunity.infrastructure.attachment.S3FileStore;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class S3FileStoreTest {

    @Mock
    AmazonS3 amazonS3;

    S3FileStore fileStore;

    @BeforeEach
    void setUp() {
        fileStore = new S3FileStore(amazonS3);
    }

    @Test
    void 파일을_저장한다() {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});

        //when
        StoredFile storedFile = fileStore.storeFile(multipartFile);

        //then
        assertThat(storedFile).isNotNull();
        assertThat(storedFile.getOriginalFilename()).isEqualTo("image.png");
        verify(amazonS3, times(1)).putObject(eq(null), anyString(), any(InputStream.class), any());
    }

    @Test
    void 파일_저장_시_유효하지_않은_파일이면_예외를_던진다() {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "image/png", new byte[]{});

        //when
        //then
        assertThatThrownBy(() -> fileStore.storeFile(multipartFile))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("파일 정보가 없습니다.");
    }

    @Test
    void 파일_저장_시_오류가_발생하면_런타임_예외를_던진다() throws Exception {
        //given
        MockMultipartFile multipartFile = spy(new MockMultipartFile("file", "image.png", "image/png", new byte[]{1}));
        when(multipartFile.getInputStream()).thenThrow(new IOException());

        //when
        //then
        assertThatThrownBy(() -> fileStore.storeFile(multipartFile))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("파일 저장을 실패했습니다.");
    }
}