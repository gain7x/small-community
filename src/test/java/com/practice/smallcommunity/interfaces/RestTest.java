package com.practice.smallcommunity.interfaces;

import com.practice.smallcommunity.config.SecurityConfig;
import com.practice.smallcommunity.security.TestSecurityConfig;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigureRestDocs
@Import({SecurityConfig.class, TestSecurityConfig.class})
public @interface RestTest {

}
