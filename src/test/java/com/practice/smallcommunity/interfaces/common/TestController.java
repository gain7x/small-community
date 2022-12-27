package com.practice.smallcommunity.interfaces.common;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() throws Exception {
        return "test";
    }

    @PostMapping("/test")
    public String test2(@RequestBody @Valid TestUser testUser) {
        return "test2";
    }

    @Data
    public static class TestUser {
        @NotBlank
        @Size(min = 5)
        private String name;
    }
}
