package com.practice.smallcommunity.domain.attachment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import com.practice.smallcommunity.infrastructure.attachment.LocalFileStore;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class LocalFileStoreTest {

    FileStore fileStore = new LocalFileStore();

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "https://localhost:8443/api/v1/images");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void 파일을_저장한다() throws IOException {
        //given
        MockMultipartFile multipartFile = spy(
            new MockMultipartFile("image",
                "some.png",
                "image/png", new byte[1]));
        doNothing().when(multipartFile).transferTo(any(File.class));

        //when
        //then
        assertThatNoException().isThrownBy(() -> fileStore.storeFile(multipartFile));
    }

    @Test
    void 파일정보가_없으면_예외를_던진다() {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("image",
            "some.png",
            "image/png", (byte[]) null);

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> fileStore.storeFile(multipartFile));
    }

    @Test
    void 파일_저장을_실패하면_예외를_던진다() throws IOException {
        //given
        MockMultipartFile multipartFile = spy(
            new MockMultipartFile("image",
                "some.png",
                "image/png", new byte[1]));
        doThrow(new IOException("")).when(multipartFile).transferTo(any(File.class));

        //when
        //then
        assertThatThrownBy(() -> fileStore.storeFile(multipartFile))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 파일에_접근하는_URI를_반환한다() {
        //when
        String result = fileStore.getAccessUri("objectKey");

        //then
        assertThat(result).startsWith("file:");
    }
}