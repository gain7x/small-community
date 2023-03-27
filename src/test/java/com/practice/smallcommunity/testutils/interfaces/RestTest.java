package com.practice.smallcommunity.testutils.interfaces;

import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigureRestDocs
@ConfigureWebSecurity
public @interface RestTest {

}
