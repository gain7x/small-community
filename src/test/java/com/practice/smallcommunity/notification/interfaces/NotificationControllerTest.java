package com.practice.smallcommunity.notification.interfaces;

import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.generateDocument;
import static com.practice.smallcommunity.testutils.interfaces.RestDocsHelper.pageData;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.notification.NotificationService;
import com.practice.smallcommunity.member.Member;
import com.practice.smallcommunity.notification.Notification;
import com.practice.smallcommunity.notification.NotificationRepository;
import com.practice.smallcommunity.notification.NotificationType;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.testutils.interfaces.RestTest;
import com.practice.smallcommunity.testutils.interfaces.WithMockMember;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    NotificationService notificationService;

    @MockBean
    NotificationRepository notificationRepository;

    @Test
    @WithMockMember
    void 유효한_알림_조회() throws Exception {
        //given
        Member receiver = DomainGenerator.createMember("A");
        Post post = spy(DomainGenerator.createPost(null, receiver, "게시글"));

        Notification notification = spy(Notification.builder()
            .receiver(receiver)
            .sender("UserB")
            .relatedPost(post)
            .type(NotificationType.REPLY)
            .build());

        when(post.getId()).thenReturn(1L);
        when(notification.getId()).thenReturn(1L);
        when(notification.getCreatedDate()).thenReturn(LocalDateTime.now());

        when(notificationRepository.findRecentNotifications(eq(1L), any()))
            .thenReturn(new PageImpl<>(List.of(notification)));

        //when
        ResultActions result = mvc.perform(get("/api/v1/notifications")
            .accept(MediaType.APPLICATION_JSON));

        //then
        String notificationTypes = Arrays.stream(NotificationType.values())
            .map(Enum::name)
            .collect(Collectors.joining(","));

        result.andExpect(status().isOk())
            .andDo(generateDocument("notification",
                requestParameters(
                    parameterWithName("page").optional().description("페이지 번호"),
                    parameterWithName("size").optional().description("페이지 크기")
                ),
                responseFields(
                    pageData(),
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("알림 ID"),
                    fieldWithPath("sender").type(JsonFieldType.STRING).description("알림 송신자"),
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("관련 게시글 ID"),
                    fieldWithPath("type").type(JsonFieldType.STRING)
                        .description("알림 유형( " + notificationTypes + " )"),
                    fieldWithPath("read").type(JsonFieldType.BOOLEAN).description("읽음 여부"),
                    fieldWithPath("createdDate").type(JsonFieldType.STRING).description("알림 일시")
                ))
            );
    }

    @Test
    @WithMockMember
    void 알림_읽음_처리() throws Exception {
        //when
        ResultActions result = mvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/notifications/{notificationId}", 1L));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("notification",
                pathParameters(
                    parameterWithName("notificationId").description("알림 ID")
                ))
            );
    }

    @Test
    @WithMockMember
    void 읽지_않은_알림_모두_읽음_처리() throws Exception {
        //when
        ResultActions result = mvc.perform(patch("/api/v1/notifications"));

        //then
        result.andExpect(status().isNoContent())
            .andDo(generateDocument("notification"));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        NotificationMapper mapper() {
            return new NotificationMapperImpl();
        }
    }
}