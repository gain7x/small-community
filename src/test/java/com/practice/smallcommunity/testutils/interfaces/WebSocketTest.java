package com.practice.smallcommunity.testutils.interfaces;

import com.practice.smallcommunity.config.WebSocketConfig;
import com.practice.smallcommunity.config.WebSocketSecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ConfigureWebSecurity
@Import({WebSocketConfig.class, WebSocketSecurityConfig.class})
public @interface WebSocketTest {
}
