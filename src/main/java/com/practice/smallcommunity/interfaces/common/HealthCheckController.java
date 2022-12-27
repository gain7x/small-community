package com.practice.smallcommunity.interfaces.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping
    public String check() {
        return "up";
    }
}
