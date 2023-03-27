package com.practice.smallcommunity.testutils.interfaces;


import com.practice.smallcommunity.config.SecurityConfig;
import com.practice.smallcommunity.security.TestSecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SecurityConfig.class, TestSecurityConfig.class})
public @interface ConfigureWebSecurity {
}
