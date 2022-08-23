package com.practice.smallcommunity.thirdparty.violation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Locale;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class SpringValidatorTest {

    ReloadableResourceBundleMessageSource messageSource;
    Validator validator;

    @BeforeEach
    void beforeEach() {
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages");
        messageSource.setDefaultEncoding("UTF-8");

        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setValidationMessageSource(messageSource);
        factoryBean.afterPropertiesSet();
        validator = factoryBean;
    }

    @Test
    void 엔티티가_제약조건을_지키면_위반_항목이_없다() {
        //given
        TestEntity entity = new TestEntity("text");

        //when
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "testEntity");
        validator.validate(entity, bindingResult);

        //then
        assertThat(bindingResult.hasErrors()).isFalse();
    }

    @Test
    void 엔티티가_제약조건을_위배하면_위반_항목이_존재한다() {
        //given
        TestEntity entity = new TestEntity("");

        //when
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "testEntity");
        validator.validate(entity, bindingResult);

        //then
        assertThat(bindingResult.hasErrors()).isTrue();
    }

    @Test
    void 메시지소스_연동() {
        //given
        TestEntity entity = new TestEntity("");

        //when
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "test");
        validator.validate(entity, bindingResult);

        ObjectError error = bindingResult.getAllErrors().get(0);

        //then
        assertThatNoException().isThrownBy(
            () -> messageSource.getMessage(error, Locale.getDefault()));
    }

    @Getter
    @AllArgsConstructor
    static class TestEntity {

        @NotBlank
        private String text;
    }
}
