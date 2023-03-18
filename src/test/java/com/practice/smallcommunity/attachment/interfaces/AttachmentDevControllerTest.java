package com.practice.smallcommunity.attachment.interfaces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.attachment.domain.FileStore;
import com.practice.smallcommunity.testutils.interfaces.RestTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(AttachmentDevController.class)
class AttachmentDevControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FileStore fileStore;

    @Test
    void 로컬_파일_저장소로_이미지를_제공한다() throws Exception {
        //given
        when(fileStore.getAccessUri(any())).thenReturn("file:/");

        //when
        ResultActions result = mvc.perform(get("/api/v1/images/folder/file")
            .contentType(MediaType.IMAGE_PNG));

        //then
        result.andExpect(status().isOk());
    }
}