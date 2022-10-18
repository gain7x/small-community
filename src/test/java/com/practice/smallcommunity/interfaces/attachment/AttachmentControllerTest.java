package com.practice.smallcommunity.interfaces.attachment;

import static com.practice.smallcommunity.interfaces.RestDocsHelper.baseData;
import static com.practice.smallcommunity.interfaces.RestDocsHelper.generateDocument;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.application.attachment.AttachmentService;
import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.attachment.FileStore;
import com.practice.smallcommunity.domain.attachment.StoredFile;
import com.practice.smallcommunity.interfaces.RestTest;
import com.practice.smallcommunity.interfaces.WithMockMember;
import com.practice.smallcommunity.utils.DomainGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    FileStore fileStore;

    @MockBean
    AttachmentService attachmentService;

    @Test
    @WithMockMember
    void 이미지_업로드() throws Exception {
        //given
        when(memberService.findByUserId(1L)).thenReturn(DomainGenerator.createMember("A"));

        StoredFile storedFile = StoredFile.builder()
            .bucket("bucket")
            .originalFilename("sample.png")
            .url("http://localhost:8080/bucket/sample.png")
            .build();

        when(fileStore.storeFile(any())).thenReturn(storedFile);
        when(attachmentService.upload(any())).thenReturn(1L);

        MockMultipartFile imageFile = new MockMultipartFile("file",
            "sample.png",
            "image/png",
            "<<png data>>".getBytes());

        //when
        ResultActions result = mvc.perform(multipart("/api/v1/images")
            .file(imageFile)
            .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated())
            .andDo(generateDocument("attachment",
                requestParts(
                    partWithName("file").description("이미지 파일")
                ),
                responseFields(
                    baseData(),
                    fieldWithPath("url").type(JsonFieldType.STRING)
                        .description("업로드된 파일에 접근 가능한 URL")
                )));
    }
}