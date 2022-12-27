package com.practice.smallcommunity.interfaces.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.practice.smallcommunity.interfaces.RestTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RestTest
@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void 서버상태() throws Exception {
        //when
        ResultActions result = mvc.perform(get("/"));

        //then
        result.andExpect(status().isOk());
    }
}